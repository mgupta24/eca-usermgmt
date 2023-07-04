package com.eca.usermgmt.controller;

import com.eca.usermgmt.dto.request.UserLoginRequest;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.service.impl.CustomUserDetailsService;
import com.eca.usermgmt.utils.GenerateJwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@CrossOrigin
public class LoginController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private GenerateJwtToken generateJwtToken;

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> token(@Valid @RequestBody UserLoginRequest loginRequest) {
		log.info("LoginController::token {} ",loginRequest);
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
		);
		var userDetails = (User) userDetailsService.loadUserByUsername(loginRequest.getUsername());
		log.info("UserDetails LoadUserByUsername {} ",userDetails);
		var token = generateJwtToken.generateToken(userDetails);
		log.info("UserDetails token {} ",token);
		return ResponseEntity.ok(Map.of("token",token));
	}
}
