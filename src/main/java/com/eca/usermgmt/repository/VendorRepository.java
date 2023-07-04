package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
