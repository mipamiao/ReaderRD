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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class PageCacheAspect {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheRoot)")
	public void pointcutRoot(){};

	@Pointcut("@annotation(com.mipa.common.annotation.PageCacheChild)")
	public void pointcutChild(){};

	@Around("pointcutRoot()")
	public Object aroundAdviceRoot(ProceedingJoinPoint joinPoint) throws Throwable{
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheRoot pageCache = signature.getMethod().getAnnotation(PageCacheRoot.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);

		if(!(args[0] instanceof Integer num && args[1] instanceof Integer size))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);

		var key = String.join("_", pageCache.fieldName(), Integer.toString(num), Integer.toString(size));
		Object object = redisTemplate.opsForValue().get(key);
		if (object == null) {
			Object result = joinPoint.proceed();
			inCache(pageCache.fieldName(), key, result);
			return result;
		}
		return object;

	}

	@Around("pointcutChild()")
	public Object aroundAdviceChild(ProceedingJoinPoint joinPoint) throws Throwable{
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		PageCacheChild pageCache = signature.getMethod().getAnnotation(PageCacheChild.class);

		if (!verifyParam(pageCache))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_MISMATCH);

		if(!(args[0] instanceof String id))
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.PAGE_CACHE_PARAM_CONFLICT);
		String itemKey = String.join("_", pageCache.fieldName(), id);
		outCache(itemKey);
		return joinPoint.proceed();
	}


	private static final Logger log = LoggerFactory.getLogger(PageCacheAspect.class);

	private void inCache(String field, String key, Object object) {
		if (!(object instanceof PageRecord<?> pageRecord)) {
			log.warn("inCache: 传入对象不是 PageRecord 类型，key = {}", key);
			return;
		}

		if (pageRecord.getDatas().isEmpty()) {
			log.info("inCache: PageRecord 数据为空，无需缓存，key = {}", key);
			return;
		}

		var keyWithTimeStamp = new TimeStamp<>(key);


		redisTemplate.opsForValue().set(key, pageRecord);
		redisTemplate.opsForValue().set(getTimeStampKeyFromOriginKey(key), keyWithTimeStamp.timeStamp);
		log.info("inCache: 缓存分页对象，key = {}", key);

		for (Object o : pageRecord.getDatas()) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor("id", o.getClass());
				Method getter = pd.getReadMethod();
				Object idValue = getter.invoke(o);
				if (idValue != null) {
					String itemKey = field + "_" + idValue;
					redisTemplate.opsForValue().set(itemKey, keyWithTimeStamp);
					log.debug("inCache: 元素缓存关联，itemKey = {}, pageKey = {}", itemKey, key);
				} else {
					log.warn("inCache: 元素 id 为 null，跳过缓存关联，元素 = {}", o);
				}
			} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
				log.error("inCache: 获取元素 id 失败，元素 = {}", o, e);
			}
		}
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
		return !pageCache.fieldName().isEmpty();
	}

	private boolean verifyParam(PageCacheChild pageCache) {
		return !pageCache.fieldName().isEmpty();
	}

	private String getTimeStampKeyFromOriginKey(String originKey){
		return "time_stamp_" + originKey;
	}


}
