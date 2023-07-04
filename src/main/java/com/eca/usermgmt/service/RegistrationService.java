package com.eca.usermgmt.service;

import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.dto.response.UserCommonResponse;
import org.springframework.http.ResponseEntity;

public interface RegistrationService {
    ResponseEntity<UserCommonResponse> registration(UserRegistrationRequest userRequest);
}
