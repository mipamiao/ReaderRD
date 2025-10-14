package com.mipa.common.config;


import com.mipa.common.utils.ScheduledThreadPoolWithMaxSize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

	@Value("${my-settings.content-page-cache.save-to-db-thread-num}")
	public Integer contentPageCacheSaveToDBThreadNum;


	@Value("${my-settings.redis-lock.watch-dog-thread-num}")
	public Integer redisLockWatchDogThreadNum;

	@Bean
	public ScheduledThreadPoolWithMaxSize contentPageCacheSaveToDBThreadPool(){
		return new ScheduledThreadPoolWithMaxSize(contentPageCacheSaveToDBThreadNum,
				"ContentPageCacheSaveToDBService", 1000);
	}

	@Bean
	public ScheduledThreadPoolWithMaxSize redisWatchDogThreadPool(){
		return new ScheduledThreadPoolWithMaxSize(redisLockWatchDogThreadNum,
				"ContentPageCacheSaveToDBService", 1000);
	}
}
