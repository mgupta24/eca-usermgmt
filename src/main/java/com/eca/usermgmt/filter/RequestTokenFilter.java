package com.eca.usermgmt.filter;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.exception.JwtTokenException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eca.usermgmt.constants.UserConstants.TOKEN_PREFIX;

/**
 * Check each & every request that have jwt token are not.
 */
@Slf4j
public class RequestTokenFilter extends OncePerRequestFilter {
	@Autowired
	private SecretKey secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		var authorizationHeader = request.getHeader(UserConstants.HEADER_AUTHORIZATION);
		log.info("RequestTokenFilter:: doFilterInternal authorizationHeader {}",authorizationHeader);
		if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
			var token = authorizationHeader.substring(TOKEN_PREFIX.length());
			if(StringUtils.isNotBlank(token)) {
				var claims = Jwts.parserBuilder()
						.setSigningKey(secretKey)
						.build()
						.parseClaimsJws(token)
						.getBody();
				if (!isTokenExpire(claims.getExpiration())) {
					var username = String.valueOf(claims.get(UserConstants.USERNAME));
					var authorities = (List<Map<String, Object>>)  claims.get(UserConstants.AUTHORITIES);
					var grantedAuthoritySet = authorities.stream()
							.filter(data -> data.containsKey(UserConstants.AUTHORITY))
							.map(authority -> new SimpleGrantedAuthority((String) authority.get(UserConstants.AUTHORITY)))
							.collect(Collectors.toSet());
					log.info("JwtTokenFilter authorities {} ",authorities);
					var authentication = new UsernamePasswordAuthenticationToken(username,null,grantedAuthoritySet);
					SecurityContextHolder.getContext()
							.setAuthentication(authentication);
				} else {
					var jwtTokenIsExpired = "Jwt Token is Expired";
					log.error(jwtTokenIsExpired);
					throw new JwtTokenException(jwtTokenIsExpired);
				}
			} else {
				log.error("Token cannot is null {} , Headers {} ",token,authorizationHeader);
				throw new JwtTokenException("Token is Null"+token);
			}
		}
		filterChain.doFilter(request, response);
	}

	public boolean isTokenExpire(Date expirationDate) {
		return  expirationDate.before(new Date());
	}
}
