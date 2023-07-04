package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.dto.UserRegistrationResponseDTO;
import com.eca.usermgmt.dto.response.UserCommonResponse;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.service.RegistrationService;
import com.eca.usermgmt.service.factory.UserTypeFactory;
import com.eca.usermgmt.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserTypeFactory userTypeFactory;

    @Autowired
    private JsonUtils jsonUtils;
    @Override
    public ResponseEntity<UserCommonResponse> registration(UserRegistrationRequest userRequest){
        if (userRequest !=null && StringUtils.isNotBlank(userRequest.getType())) {
            return toResponseEntity(userTypeFactory.createUserType(userRequest));
        }
        throw new UserManagementException(UserConstants.INVALID_USER_TYPE);
    }


    private ResponseEntity<UserCommonResponse> toResponseEntity(UserRegistrationResponseDTO registration) {
        var userCommonResponse = new UserCommonResponse();
        userCommonResponse.setTimestamp(LocalDateTime.now());
        userCommonResponse.setResponseDto(registration);
        return new ResponseEntity<>(userCommonResponse, HttpStatus.CREATED);
    }
}
