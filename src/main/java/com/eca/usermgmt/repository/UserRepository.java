package com.eca.usermgmt.repository;

import com.eca.usermgmt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	User findByUserPhoneNumber(Long userPhoneNumber);
	@Query("SELECT user FROM User user WHERE user.tenant.id = :tenantsId")
	User getUserDetailsByTenantId(@Param("tenantsId") Long id);

	@Query("SELECT user FROM User user WHERE user.owner.id = :ownerId")
	User getUserDetailsByOwnerId(@Param("ownerId") Long ownerId);

	@Query("SELECT user FROM User user WHERE user.vendor.id = :vendorId")
	User getUserDetailsByVendorId(@Param("vendorId") Long vendorId);

	@Query("SELECT usr FROM User usr WHERE usr.owner.type = :type")
	List<User> getAllOwnerByType(@Param("type") String  type);

	@Query("SELECT usr FROM User usr WHERE usr.vendor.type = :type")
	List<User> getAllVendorByType(@Param("type") String  type);

	@Query("SELECT usr FROM User usr WHERE usr.tenant.type = :type")
	List<User> getAllTenantByType(@Param("type") String  type);

	Optional<User> findByUsername(String username);
}
