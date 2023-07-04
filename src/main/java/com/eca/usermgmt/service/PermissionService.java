package com.eca.usermgmt.service;

import com.eca.usermgmt.entity.Permission;

public interface PermissionService {
	Permission getPermissionByName(String name);

	Permission createPermissionIfNotFound(String name);
}
