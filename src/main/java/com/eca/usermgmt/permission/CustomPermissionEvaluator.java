package com.eca.usermgmt.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {
	public boolean hasPermission(Authentication authentication, Object target, Object permission) {
		return hasPermission(authentication, permission);
	}

	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
	                             Object permission) {
		return hasPermission(authentication, permission);
	}

	private  boolean hasPermission(final Authentication authentication, final Object permission) {
		if ((authentication == null) || !(permission instanceof String)) {
			log.warn("Authentication {} is null or permission {} is not configured", authentication, permission);
			return false;
		}
		return authentication
				.getAuthorities()
				.stream()
				.anyMatch(ga -> ga.getAuthority().equals(permission));
	}
}
