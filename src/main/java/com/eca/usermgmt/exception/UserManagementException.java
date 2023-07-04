package com.eca.usermgmt.exception;

import org.apache.commons.lang3.StringUtils;

public class UserManagementException extends RuntimeException{

	public UserManagementException(String message) {
		super(message);
	}
	public UserManagementException(String message, Throwable throwable) {
		super(message,throwable);
	}

	public UserManagementException(String... args) {
		super(StringUtils.joinWith(" ",args));
	}

}
