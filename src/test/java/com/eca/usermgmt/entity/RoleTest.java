package com.eca.usermgmt.entity;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoleTest {

	@Test
	void roleAuthorityWithPermissionsTest() {
		var role = getRole();
		role.setPermissions(List.of(new Permission("WRITE")));
		assertThat(role.getAuthority()).isNotNull()
				.contains("WRITE")
				.doesNotContain("ROLE_TEST");
	}
	@Test
	void roleAuthorityFormRoleTest() {
		var role = getRole();
		assertThat(role.getAuthority())
				.isNotNull()
				.doesNotContain("WRITE")
				.contains("ROLE_TEST");
	}

	private static Role getRole() {
		var role = new Role();
		role.setRoleName("ROLE_TEST");
		role.setId(101L);
		role.setUpdatedOn();
		return role;
	}
}
