package com.eca.usermgmt.controller;

import com.eca.usermgmt.dto.request.UserLoginRequest;
import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.entity.Role;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.Permissions;
import com.eca.usermgmt.service.impl.CustomUserDetailsService;
import com.eca.usermgmt.utils.GenerateJwtToken;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Slf4j
@TestPropertySource(properties = {"app.jwt.expirationTime=1734431739"})
@ContextConfiguration(classes = {LoginController.class,GenerateJwtToken.class,
		LoginControllerTest.SecretTestConfig.class})
class LoginControllerTest {

	@TestConfiguration
	public static class SecretTestConfig {
		@Bean
		public SecretKey secretKey() {
			return Keys.hmacShaKeyFor("Vfj69nm&^%3KjinRvFVSKJSJKJKhjsd87812jkJKJKLAAJK".getBytes());
		}
	}

	@MockBean
	private AuthenticationManager authenticationManager;
	@MockBean
	private CustomUserDetailsService userDetailsService;
	@Autowired
	private  LoginController loginController;

	@Test
	void tokenTest() {
		var user = getUser();
		Mockito.when(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString())).thenReturn(user);
		ResponseEntity<Map<String, String>> response = loginController.token(UserLoginRequest.of("test", "test"));
		assertThat(response).isNotNull();
		var body = response.getBody();
		assertThat(body).isNotNull();
		var token = body.get("token");
		log.info("{}",token);
		assertThat(token).isNotNull();

	}

	private User getUser() {
		var write = new Permission(Permissions.WRITE_PERMISSION.name());
		var update = new Permission(Permissions.UPDATE_PERMISSION.name());
		var user = new User();
		user.setUsername("test");
		var e1 = new Role();
		e1.setRoleName("OWNER");
		e1.setPermissions(List.of(write,update));
		user.setRoles(Set.of(e1));
		user.setEnabled(true);
		user.setCredentialsNonExpired(true);
		return user;
	}
}
