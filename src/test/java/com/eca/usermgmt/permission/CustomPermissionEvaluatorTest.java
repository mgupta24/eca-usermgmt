package com.eca.usermgmt.permission;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.context.LoadRepositoryContext;
import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.entity.Role;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.Permissions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomPermissionEvaluator.class, LoadRepositoryContext.class})
class CustomPermissionEvaluatorTest {

	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Test
	void authenticationNullTest() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertThat(permissionEvaluator.hasPermission(authentication,"101L","WRITE")).isFalse();
	}

	@Test
	void authenticationWithAuthorityTest() {
		var principal = getUser();
		var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(),
				principal.getAuthorities());
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		assertThat(permissionEvaluator.hasPermission(authentication,"101L", UserConstants.ROLE_OWNER)).isTrue();
	}

	private static User getUser() {
		var user = new User();
		user.setUsername("test");
		var e1 = new Role();
		e1.setRoleName(UserConstants.ROLE_OWNER);
		var write = new Permission();
		write.setPermissionName(Permissions.WRITE_PERMISSION.name());
		var update = new Permission();
		update.setPermissionName(Permissions.UPDATE_PERMISSION.name());
		e1.setPermissions(List.of(write,update));
		user.setRoles(Set.of(e1));
		user.setEnabled(true);
		user.setCredentialsNonExpired(true);
		user.setPassword("$2a$10$/QnTOj3aw3LtOpq1XtVrIuEn0Mt/JZejf6FTDWK.bcROMRjdc2uyi");
		return user;
	}
}
