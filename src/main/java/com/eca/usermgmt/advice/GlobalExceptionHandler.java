package com.eca.usermgmt.advice;

import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.response.UserCommonResponse;
import com.eca.usermgmt.exception.JwtTokenException;
import com.eca.usermgmt.exception.TenantNotFoundException;
import com.eca.usermgmt.exception.UserManagementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ConstraintViolationException.class,DataIntegrityViolationException.class})
	public ResponseEntity<UserCommonResponse> constraintViolationException(Exception ex) {
		log.error("CustomGlobalExceptionHandler constraintViolationException {} ",ex.getMessage());
		if(ex instanceof DataIntegrityViolationException) {
			var rootCause = ((DataIntegrityViolationException) ex).getRootCause();
			if(rootCause !=null && rootCause.getMessage() !=null) {
				return new ResponseEntity<>(customResponse(rootCause.getMessage()), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(customResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler({UserManagementException.class})
	public ResponseEntity<UserCommonResponse> userManagementException(Exception ex) {
		log.error("CustomGlobalExceptionHandler UserManagementException {} ",ex.getMessage());
		return new ResponseEntity<>(customResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler({AuthenticationException.class,AccessException.class})
	public ResponseEntity<UserCommonResponse> handleAuthenticationException(Exception ex, HttpServletResponse response) {
		log.error("CustomGlobalExceptionHandler handleAuthenticationException {} ",ex.getMessage());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return new ResponseEntity<>(customResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler({TenantNotFoundException.class, JwtTokenException.class})
	public ResponseEntity<UserCommonResponse> notFound(Exception ex) {
		log.error("CustomGlobalExceptionHandler Not Found {} ",ex.getMessage());
		return new ResponseEntity<>(customResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<BaseResponse> anyException(Exception ex) {
		log.error("CustomGlobalExceptionHandler Exception {} ",ex.getMessage());
		return new ResponseEntity<>(customResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private UserCommonResponse customResponse(String message) {
		var customResponse = new UserCommonResponse();
		customResponse.setTimestamp(LocalDateTime.now());
		customResponse.setError(message);
		return customResponse;
	}

}
