package com.mipa.service;

import com.mipa.common.callback.TaskInLock;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.utils.ScheduledThreadPoolWithMaxSize;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class ContentPageCacheSaveToDBService {

	@Autowired
	private TopOfContentPageService topOfContentPageService;

	@Autowired
	private RedisLockServer redisLock;

	@Autowired
	private ContentPageCacheService pageCacheService;

	@Autowired
	private MyConfiguration config;


	private static final String lockKeyPrefix = "ContentPageCacheServiceLockKey_";


	@Resource(name = "contentPageCacheSaveToDBThreadPool")
	private ScheduledThreadPoolWithMaxSize contentPageCacheSaveToDBThreadPool;

	public void saveToDB() {
		Integer saveToDBThreadNum = contentPageCacheSaveToDBThreadPool.getThreadNum();
		var keys = pageCacheService.getAllActiveKeys();
		var onesNum = Math.ceil(1.0f * keys.size() / saveToDBThreadNum);
		for (Integer threadIndex = 0; threadIndex < saveToDBThreadNum; threadIndex++) {
			Integer finalThreadIndex = threadIndex;
			contentPageCacheSaveToDBThreadPool.scheduleOneShot("saveToDB" + threadIndex, new Runnable() {
				@Override
				public void run() {
					for (int i = finalThreadIndex; i < keys.size(); i += saveToDBThreadNum) {
						int finalI = i;
						var key = keys.get(i);
						redisLock.tryLock(getLockKey(key), "false", 5, new TaskInLock() {
							@Override
							public void run() {
								var writerCommandSet = pageCacheService.getWriterCommandSet(keys.get(finalI));
								long secondsDiff = Duration.between(writerCommandSet.getTimeStamp(), LocalDateTime.now()).getSeconds();
								if (secondsDiff > config.contentPageCacheSaveToDBIdleTime)
									topOfContentPageService.scheduleOp(
											writerCommandSet, writerCommandSet.getUserId(), writerCommandSet.getChapterId(), false);
							}
						}, TimeUnit.SECONDS);
					}
				}
			}, 0, TimeUnit.SECONDS);
		}
	}

	private String getLockKey(String originKey){
		return lockKeyPrefix + originKey;
	}
}
