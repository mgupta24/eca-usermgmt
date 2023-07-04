package com.eca.usermgmt.exception;

import org.apache.commons.lang3.StringUtils;

public class TenantNotFoundException extends RuntimeException {
	public TenantNotFoundException(Long id) {
		super(StringUtils.join("Tenant with id ", id, " not found."));
	}
}
