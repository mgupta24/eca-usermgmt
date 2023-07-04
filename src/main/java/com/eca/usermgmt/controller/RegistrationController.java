package com.eca.usermgmt.controller;

import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.dto.response.UserCommonResponse;
import com.eca.usermgmt.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/v1/users")
@CrossOrigin
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private Validator validator;

    @PostMapping("/registration")
    public ResponseEntity<UserCommonResponse> registration(
            @RequestBody UserRegistrationRequest userRequest) {
        log.info("RegistrationController::registration request from UI {} ",userRequest);
        Set<ConstraintViolation<UserRegistrationRequest>> constraintViolations = validator.validate(userRequest);
        if (!constraintViolations.isEmpty()) {
            log.error("RegistrationController::registration constraintViolations errors list {}",constraintViolations);
            throw new ConstraintViolationException(constraintViolations);
        }
        return registrationService.registration(userRequest);
    }

}
