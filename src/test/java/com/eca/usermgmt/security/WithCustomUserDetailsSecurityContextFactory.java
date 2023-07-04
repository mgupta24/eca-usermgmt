package com.eca.usermgmt.security;

import com.eca.usermgmt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;

public class WithCustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<CustomUserDetails> {

	@Autowired
	private UserDetailsService userDetailsService;
	@Override
	public SecurityContext createSecurityContext(CustomUserDetails annotation) {
		var username = annotation.value();
		Assert.hasLength(username, "username cannot be empty !!!!");
		var principal = (User) userDetailsService.loadUserByUsername(username);
		var authentication = UsernamePasswordAuthenticationToken.authenticated(principal,
				principal.getPassword(), principal.getAuthorities());
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		return context;
	}
}
