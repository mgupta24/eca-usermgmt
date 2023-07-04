package com.eca.usermgmt.utils;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GenerateJwtToken {

	@Autowired
	private SecretKey secretKey;

	@Value("${app.jwt.expirationTime}")
	private long expirationTime;

	public String generateToken(User userDetails) {
		var claims = getPermissionsAuthority(userDetails);
		return Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setIssuer("mgupta")
				.setSubject(userDetails.getUsername())
				.setClaims(claims)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(secretKey)
				.compact();
	}

	private  HashMap<String, Object> getPermissionsAuthority(User userDetails) {
		var claims = new HashMap<String, Object>();
		var grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();
		userDetails.getRoles()
						.forEach(role -> {
							grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoleName()));
							grantedAuthorities.addAll(getPermissions(role.getPermissions()));
						});
		claims.put(UserConstants.AUTHORITIES,grantedAuthorities);
		claims.put(UserConstants.USERNAME, userDetails.getUsername());
		return claims;
	}

	private  List<SimpleGrantedAuthority> getPermissions(Collection<Permission> role) {
		return role.stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
				.collect(Collectors.toList());
	}
}
