package com.eca.usermgmt.exception;

public class UserRegistrationFailed extends RuntimeException {
	public UserRegistrationFailed(String message) {
		super(message);
	}
	public UserRegistrationFailed(String message,Throwable throwable) {
		super(message,throwable);
	}
}
