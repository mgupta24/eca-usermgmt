package com.eca.usermgmt.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnExpression("${app.cache.enabled:false}")
public class CacheServiceImpl implements CacheService{

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void addToCache(String key, Object value, long timeout, TimeUnit timeUnit) {
		redisTemplate.opsForValue().set(key,value,timeout,timeUnit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getFromCache(String key) {
		return (T) redisTemplate.opsForValue().get(key);
	}

	@Override
	public void remove(String key) {
		redisTemplate.opsForValue().getOperations().delete(key);
	}
}
