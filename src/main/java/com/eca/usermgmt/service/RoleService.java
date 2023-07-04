package com.eca.usermgmt.service;

import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.entity.Role;

import java.util.List;

public interface RoleService {
	Role saveRole(Role role);

	Role getRoleByName(String roleName);

	Role createIfNotFound(String roleName, List<Permission> permissions);
}
