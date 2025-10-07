package com.mipa.common.aspect;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.annotation.PageCacheChild;
import com.mipa.common.annotation.PageCacheCut;
import com.mipa.common.annotation.PageCacheRoot;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.utils.ParamFill;
import com.mipa.common.utils.TimeStamp;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.SerializationUtils.serialize;

@Aspect
@Component
public class PageCacheAspect {

	private static final String timeStampHashKey = "timeStampHashKey";

	private static final String seqChar = ":";

	private static final String lockKeyPrefix = "lockKey";

	private static final String timeStampKeyPrefix = "timeStampKey" + seqChar;

	private static final String RelationInfo = "RelationInfo";

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private GenericJackson2JsonRedisSerializer jsonSerializer;

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheRoot)")
	public void pointcutRoot(){};

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheChild)")
	public void pointcutChild(){};

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheCut)")
	public void pointcutCut(){};

	@Around("pointcutRoot()")
	public Object aroundAdviceRoot(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheRoot pageCache = signature.getMethod().getAnnotation(PageCacheRoot.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);


		var paramMap = ParamFill.transToMap(args);

		if (!(paramMap.get(pageCache.pageNumberParamIndex()) instanceof Integer num
				&& paramMap.get(pageCache.pageSizeParamIndex()) instanceof Integer size))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);

		var fieldName = pageCache.fieldName() + seqChar + ParamFill.run(pageCache.extraFieldInfo(), paramMap);

		var key = String.join(seqChar, fieldName, Integer.toString(size), Integer.toString(num));
		if (verifyCacheable(parseFieldNameFromKey(key), parsePageNumFromKey(key))) {
			Object object = redisTemplate.opsForValue().get(key);
			if (object == null) {
				object = distriLockToIncache(key, joinPoint, pageCache);
			}
			return object;
		} else {
			return distriLockToIncache(key, joinPoint, pageCache);
		}
	}

	@Around("pointcutChild()")
	public Object aroundAdviceChild(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheChild pageCache = signature.getMethod().getAnnotation(PageCacheChild.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);

		if (!(args.length > pageCache.idIndex() && args[0] instanceof String id))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);
		String itemKey = String.join(seqChar, pageCache.fieldName(), RelationInfo, id);
		outCache(itemKey);
		return joinPoint.proceed();
	}

	@Around("pointcutCut()")
	public Object aroundAdviceCut(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheCut pageCache = signature.getMethod().getAnnotation(PageCacheCut.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);

		if (!(args.length > pageCache.idIndex() && args[0] instanceof String id))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);

		String itemKey = String.join(seqChar, pageCache.fieldName(), RelationInfo, id);
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(itemKey);

		if (entries.isEmpty()) {
			log.info("outCache: 没有找到关联缓存，itemKey = {}", itemKey);
			return null;
		}

		for (Map.Entry<Object, Object> entry : entries.entrySet()) {
			var key = entry.getKey().toString();
			setTopUnCacheable(parseFieldNameFromKey(key), parsePageNumFromKey(key));
		}

		return joinPoint.proceed();
	}

	private Object distriLockToIncache(String key, ProceedingJoinPoint joinPoint, PageCacheRoot pageCache) throws Throwable {
		var lockKey = lockKeyPrefix + key;
		Object object = null;
		Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
		if (Boolean.TRUE.equals(success)) {
			try {
				object = joinPoint.proceed();
				inCache(pageCache.fieldName(), pageCache.ttl(), key, object, pageCache.idName());
			} finally {
				redisTemplate.delete(lockKey);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				Thread.sleep(100);
				object = redisTemplate.opsForValue().get(key);
				if (object != null) return object;
			}
			object = joinPoint.proceed();
			inCache(pageCache.fieldName(), pageCache.ttl(), key, object, pageCache.idName());
		}
		return object;
	}


	private static final Logger log = LoggerFactory.getLogger(PageCacheAspect.class);

	private void inCache(String field, int ttl, String key, Object object, String idName) {
		if (!(object instanceof PageRecord<?> pageRecord)) {
			log.warn("inCache: 传入对象不是 PageRecord 类型，key = {}", key);
			return;
		}

		if (pageRecord.getDatas().isEmpty()) {
			log.info("inCache: PageRecord 数据为空，无需缓存，key = {}", key);
			return;
		}

		var keyWithTimeStamp = new TimeStamp<>(key);

		StringBuilder script = new StringBuilder();
		script.append("redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[2]); ");
		script.append("redis.call('set', KEYS[2], ARGV[3], 'EX', ARGV[2]); ");
		for (Object o : pageRecord.getDatas()) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor(idName, o.getClass());
				Method getter = pd.getReadMethod();
				Object idValue = getter.invoke(o);
				if (idValue != null) {
					String itemKey = String.join(seqChar, field, RelationInfo, idValue.toString());
					script.append("redis.call('hset', '").append(itemKey)
							.append("', ARGV[4], ARGV[5]);").append("redis.call('EXPIRE', '")
							.append(itemKey).append("',").append(ttl + 10).append("); ");
				}
			} catch (Exception e) {
				log.error("inCache Lua 构建失败", e);
			}
		}

		setCacheable(parseFieldNameFromKey(key), parsePageNumFromKey(key), ttl + 10);

		redisTemplate.execute((RedisCallback<Void>) connection -> {
			connection.scriptingCommands().eval(
					script.toString().getBytes(StandardCharsets.UTF_8),
					ReturnType.STATUS,
					2,
					key.getBytes(),
					getTimeStampKeyFromOriginKey(key).getBytes(),
					jsonSerializer.serialize(pageRecord),
					String.valueOf(ttl).getBytes(),
					String.valueOf(keyWithTimeStamp.timeStamp).getBytes(),
					keyWithTimeStamp.data.getBytes(),
					String.valueOf(keyWithTimeStamp.timeStamp).getBytes()
			);
			return null;
		});
		log.info("inCache Lua 执行完成");
	}

	private void outCache(String itemKey) {
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(itemKey);

		if (entries == null) {
			log.info("outCache: 没有找到关联缓存，itemKey = {}", itemKey);
			return;
		}

		for (Map.Entry<Object, Object> entry : entries.entrySet()) {
			var key = entry.getKey().toString();
			var timeStamp = redisTemplate.opsForValue().get(getTimeStampKeyFromOriginKey(key));
			if (Objects.equals((Integer) timeStamp, (Integer) entry.getValue())) {
				redisTemplate.delete(key);
				setUnCacheable(parseFieldNameFromKey(key), parsePageNumFromKey(key));
				log.debug("outCache: 删除分页缓存，pageKey = {}", key);
			}
		}

		redisTemplate.delete(itemKey);
		log.info("outCache: 删除元素关联集合，itemKey = {}", itemKey);
	}

	private boolean verifyCacheable(String fieldName, int pageNum){
		return redisTemplate.opsForZSet().score(fieldName, pageNum) != null;
	}

	private void setCacheable(String fieldName, int pageNum, int ttl){
		redisTemplate.opsForZSet().add(fieldName, pageNum, pageNum);
		redisTemplate.expire(fieldName, ttl, TimeUnit.SECONDS);
	}

	private void setUnCacheable(String fieldName, int pageNum){
		redisTemplate.opsForZSet().remove(fieldName, pageNum);
	}


	private void setTopUnCacheable(String fieldName, int pageNum){
		redisTemplate.opsForZSet().removeRangeByScore(fieldName, pageNum, Double.MAX_VALUE);
	}

	private String parseFieldNameFromKey(String key) {
		var items = key.split(seqChar);
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < items.length - 1; i++) {
			if (i != 0) stringBuilder.append(seqChar);
			stringBuilder.append(items[i]);
		}
		return stringBuilder.toString();
	}

	private int parsePageNumFromKey(String key) {
		var items = key.split(seqChar);
		return Integer.parseInt(items[items.length - 1]);
	}



	private boolean verifyParam(PageCacheRoot pageCache) {
		return !(pageCache.fieldName().isEmpty() || pageCache.ttl() <= 0);
	}

	private boolean verifyParam(PageCacheChild pageCache) {
		return !(pageCache.fieldName().isEmpty()|| pageCache.idIndex() < 0);
	}

	private boolean verifyParam(PageCacheCut pageCache) {
		return !(pageCache.fieldName().isEmpty()|| pageCache.idIndex() < 0);
	}

	private String getTimeStampKeyFromOriginKey(String originKey){
		return timeStampKeyPrefix + originKey;
	}

}
