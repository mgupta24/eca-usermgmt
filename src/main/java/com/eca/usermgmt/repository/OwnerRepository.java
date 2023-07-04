package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner,Long> {
}
