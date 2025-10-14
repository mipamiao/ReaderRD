package com.mipa.service;

import com.mipa.common.callback.TaskInLock;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.utils.ScheduledThreadPoolWithMaxSize;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockServer {


	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private MyConfiguration config;

	@Resource(name = "redisWatchDogThreadPool")
	private ScheduledThreadPoolWithMaxSize redisWatchDogThreadPool ;

	public boolean tryLock(String key, String value, Integer expire, TaskInLock task, TimeUnit timeUnit) {
		var result = redisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.SECONDS);
		if (Boolean.TRUE.equals(result)) {
			var taskName = "watch_" + value;
			redisWatchDogThreadPool.scheduleAtFixedRate(taskName ,new Runnable() {
				@Override
				public void run() {
					var newValue = redisTemplate.opsForValue().get(key);
					if(newValue.equals(value)){
						redisTemplate.expire(key, expire, timeUnit);
					}else {
						redisWatchDogThreadPool.cancleTask(taskName );
					}
				}
			}, expire/3, expire/3, timeUnit);
			task.run();
			redisWatchDogThreadPool.cancleTask(taskName);
			redisTemplate.delete(key);
			return true;
		}
		return false;
	}


}
