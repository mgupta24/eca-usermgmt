package com.eca.usermgmt.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class CacheContainer {

	private Object cacheObject;
	private long createTimeStamp;
	public static boolean isCacheNotExpired(CacheContainer container, long cacheExpiryInSeconds) {
		log.info("CacheContainer::isCacheNotExpired cache container request {} cache expiry in seconds {} ",container
				,cacheExpiryInSeconds);
		return  container !=null
				&& container.getCacheObject() != null
				&& System.currentTimeMillis() - container.getCreateTimeStamp() < cacheExpiryInSeconds;


	}
}
