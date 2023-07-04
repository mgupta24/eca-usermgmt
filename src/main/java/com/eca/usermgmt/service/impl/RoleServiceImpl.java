package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.entity.Permission;
import com.eca.usermgmt.entity.Role;
import com.eca.usermgmt.repository.RoleRepository;
import com.eca.usermgmt.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public Role getRoleByName(String roleName) {
		return roleRepository.findByRoleName(roleName);
	}

	@Transactional
	@Override
	public Role createIfNotFound(String roleName, List<Permission> permissions) {
		var roleByName = roleRepository.findByRoleName(roleName);
		log.info("RoleServiceImpl::createIfNotFound {} ",roleByName);
		if (roleByName == null) {
			roleByName = new Role();
			roleByName.setPermissions(permissions);
			roleByName.setRoleName(roleName);
			roleRepository.save(roleByName);
		}
		return roleByName;
	}
}
