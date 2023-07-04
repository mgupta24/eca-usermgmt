package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantsRepository extends JpaRepository<Tenant,Long> {
}
