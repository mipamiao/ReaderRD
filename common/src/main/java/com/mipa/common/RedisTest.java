package com.mipa.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTest implements CommandLineRunner {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public void run(String... args) throws Exception {
		redisTemplate.opsForValue().set("test", "hello");
		System.out.println(redisTemplate.opsForValue().get("test"));
	}
}
