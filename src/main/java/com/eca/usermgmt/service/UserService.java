package com.eca.usermgmt.service;

import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.entity.Owner;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.entity.Vendor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserService {

	ResponseEntity<BaseResponse> getAllUsers(String typeOfUser);
	ResponseEntity<BaseResponse> getUserByPhoneNumber(Long phoneNumber);
	User getUserDetailsByOwnerId(Long ownerId);
	User getUserDetailsByVendorId(Long vendorId);
	@Transactional(readOnly = true)
	Optional<Vendor> findVendorWithId(Long vendorId);
	Optional<Owner> getOwner(Long ownerId);
	void saveUserDetails(User user);
	User saveUserWithDefaultRoles(User user);
}
