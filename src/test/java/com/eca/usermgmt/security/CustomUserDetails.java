package com.eca.usermgmt.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithCustomUserDetailsSecurityContextFactory.class)
public @interface CustomUserDetails {

	String value() default "test";

	String[] roles() default { "ROLE_OWNER","ROLE_VENDOR","ROLE_TENANT" };

	String[] authorities() default {"WRITE_PERMISSION","READ_PERMISSION","UPDATE_PERMISSION","DELETE_PERMISSION"};

	String password() default "password";

	@AliasFor(annotation = WithSecurityContext.class)
	TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;
}
