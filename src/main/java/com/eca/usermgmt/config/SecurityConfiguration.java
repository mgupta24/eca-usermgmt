package com.eca.usermgmt.config;

import com.eca.usermgmt.filter.RequestTokenFilter;
import com.eca.usermgmt.service.impl.CustomUserDetailsService;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Value("${app.secret.key}")
	private String secretKey;

	@Bean
	public CustomUserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
	http
				.csrf()
				.disable()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests(requests -> requests
						.antMatchers("/v1/users/registration").permitAll()
						.antMatchers("/v1/users/login").permitAll()
						.antMatchers("/v1/actuator/**").permitAll()
						.antMatchers("/v1/users/**").authenticated());

		http.authenticationProvider(authenticationProvider());
		http.addFilterAfter(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
				.antMatchers("/swagger-ui/**", "/swagger-ui.html/**", "/webjars/**", "/swagger-resources/**", "/v2/api-docs/**",
						"/swagger-resources/configuration/ui/**", "/swagger-resources/configuration/security/**", "/images/**")
				.antMatchers("/h2-console/**");
	}


	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		var provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
		return authConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RequestTokenFilter jwtTokenFilter() {
		return new RequestTokenFilter();
	}

	@Bean
	public SecretKey secretKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}
}
