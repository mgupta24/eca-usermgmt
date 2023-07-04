package com.eca.usermgmt.service;

import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.TenantsDTO;
import org.springframework.http.ResponseEntity;

public interface TenantService {
	ResponseEntity<BaseResponse> getAllTenants();
	ResponseEntity<BaseResponse> getTenantById(Long id);
	ResponseEntity<BaseResponse> createTenant(TenantsDTO requestDto);
	ResponseEntity<BaseResponse> updateTenant(Long id, TenantsDTO request);
	void deleteTenant(Long id);
}
