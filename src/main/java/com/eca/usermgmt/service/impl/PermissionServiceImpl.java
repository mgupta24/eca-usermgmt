package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.repository.PermissionRepository;
import com.eca.usermgmt.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public Permission getPermissionByName(String name) {
		return permissionRepository.findByPermissionName(name);
	}

	@Override
	@Transactional
	public Permission createPermissionIfNotFound(String name) {
		var permission = permissionRepository.findByPermissionName(name);
		log.info("PermissionServiceImpl::createPermissionIfNotFound {}",permission);
		if (permission == null) {
			permission = new Permission();
			permission.setPermissionName(name);
			permissionRepository.save(permission);
		}
		return permission;
	}
}
