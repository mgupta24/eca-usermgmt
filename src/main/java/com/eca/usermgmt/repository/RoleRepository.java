package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
	Role findByRoleName(String roleName);
}
