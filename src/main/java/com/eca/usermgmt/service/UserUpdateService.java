package com.eca.usermgmt.service;

import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.VendorDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;

public interface UserUpdateService {
	ResponseEntity<BaseResponse> updateOwner(Long id, OwnerDTO ownerDTO);
	ResponseEntity<BaseResponse> updateVendor(Long vendorId, VendorDTO vendorDTO);
}
