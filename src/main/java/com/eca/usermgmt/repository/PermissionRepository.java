package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
	Permission findByPermissionName(String name);
}
