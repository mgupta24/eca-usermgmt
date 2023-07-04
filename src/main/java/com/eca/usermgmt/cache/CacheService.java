package com.eca.usermgmt.cache;

import java.util.concurrent.TimeUnit;

public interface CacheService {

	void addToCache(String key, Object value, long timeout, TimeUnit timeUnit);
	<T> T getFromCache(String key);
	void remove(String key);
}
