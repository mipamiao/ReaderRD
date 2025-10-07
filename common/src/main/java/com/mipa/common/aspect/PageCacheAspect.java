package com.mipa.common.aspect;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.annotation.PageCacheChild;
import com.mipa.common.annotation.PageCacheRoot;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.PageRecord;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.SerializationUtils.serialize;

@Aspect
@Component
public class PageCacheAspect {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private GenericJackson2JsonRedisSerializer jsonSerializer;

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheRoot)")
	public void pointcutRoot(){};

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheChild)")
	public void pointcutChild(){};

	@Around("pointcutRoot()")
	public Object aroundAdviceRoot(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheRoot pageCache = signature.getMethod().getAnnotation(PageCacheRoot.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);

		if (!(args[0] instanceof Integer num && args[1] instanceof Integer size))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);

		var key = String.join("_", pageCache.fieldName(), Integer.toString(num), Integer.toString(size));
		Object object = redisTemplate.opsForValue().get(key);
		if (object == null) {
			var lockKey = "lock_key" + key;
			Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
			if (Boolean.TRUE.equals(success)) {
				try {
					object = joinPoint.proceed();
					inCache(pageCache.fieldName(), pageCache.ttl(), key, object);
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
				inCache(pageCache.fieldName(), pageCache.ttl(), key, object);
			}
		}
		return object;
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
		String itemKey = String.join("_", pageCache.fieldName(), id);
		outCache(itemKey);
		return joinPoint.proceed();
	}


	private static final Logger log = LoggerFactory.getLogger(PageCacheAspect.class);

	private void inCache(String field, int ttl, String key, Object object) {
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
		script.append("redis.call('set', KEYS[2], ARGV[3]); ");
		for (Object o : pageRecord.getDatas()) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor("id", o.getClass());
				Method getter = pd.getReadMethod();
				Object idValue = getter.invoke(o);
				if (idValue != null) {
					String itemKey = field + "_" + idValue;
					script.append("redis.call('set', '").append(itemKey)
							.append("', ARGV[4], 'EX', ").append(ttl + 10).append("); ");
				}
			} catch (Exception e) {
				log.error("inCache Lua 构建失败", e);
			}
		}

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
					jsonSerializer.serialize(keyWithTimeStamp)
			);
			return null;
		});
	}

	private void outCache(String itemKey) {
		Object relatedKeysWithTimeStamp = redisTemplate.opsForValue().get(itemKey);


		if (relatedKeysWithTimeStamp == null) {
			log.info("outCache: 没有找到关联缓存，itemKey = {}", itemKey);
			return;
		}

		var tagWithTimeStamp = (TimeStamp<String>) relatedKeysWithTimeStamp;
		var key = tagWithTimeStamp.data;
		var timeStamp = redisTemplate.opsForValue().get(getTimeStampKeyFromOriginKey(key));
		if (Objects.equals((Integer) timeStamp, tagWithTimeStamp.timeStamp)) {
			redisTemplate.delete(key);
			log.debug("outCache: 删除分页缓存，pageKey = {}", key);
		}

		redisTemplate.delete(itemKey);
		log.info("outCache: 删除元素关联集合，itemKey = {}", itemKey);
	}



	private boolean verifyParam(PageCacheRoot pageCache) {
		return !(pageCache.fieldName().isEmpty() || pageCache.ttl() <= 0);
	}

	private boolean verifyParam(PageCacheChild pageCache) {
		return !(pageCache.fieldName().isEmpty()|| pageCache.idIndex() < 0);
	}

	private String getTimeStampKeyFromOriginKey(String originKey){
		return "time_stamp_" + originKey;
	}


}
