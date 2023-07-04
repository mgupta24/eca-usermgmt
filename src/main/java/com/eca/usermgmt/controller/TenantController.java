package com.eca.usermgmt.controller;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@CrossOrigin
public class TenantController {

	@Autowired
	private TenantService tenantService;

	@GetMapping("/tenants")
	@PreAuthorize("hasAnyAuthority('READ_PERMISSION','WRITE_PERMISSION')")
	public ResponseEntity<BaseResponse> getAllTenants() {
		log.info("TenantController::getAllTenants");
		return tenantService.getAllTenants();
	}

	@GetMapping("/tenants/{id}")
	@PreAuthorize("hasAnyAuthority('READ_PERMISSION','UPDATE_PERMISSION')")
	public ResponseEntity<BaseResponse> getTenantById(@NotNull @PathVariable Long id) {
		log.info("TenantController::getTenantById TenantId {}",id);
		return tenantService.getTenantById(id);
	}

	@PostMapping("/tenants")
	@PreAuthorize("hasAnyAuthority('READ_PERMISSION','UPDATE_PERMISSION')")
	public ResponseEntity<BaseResponse> createTenant( @Valid @RequestBody TenantsDTO request) {
		log.info("TenantController::createTenant request {} ",request);
		return tenantService.createTenant(request);
	}

	@PutMapping("/tenants/{id}")
	@PreAuthorize("hasAnyAuthority('"+UserConstants.WRITE_PERMISSION+"','"+UserConstants.UPDATE_PERMISSION+"')")
	public ResponseEntity<BaseResponse> updateTenant(@PathVariable Long id,
	                                                 @RequestBody TenantsDTO request) {
		log.info("TenantController::updateTenant Id {} Tenant request {} ",id,request);
		return tenantService.updateTenant(id,request );
	}

	@DeleteMapping("/tenants/{id}")
	@PreAuthorize("hasAnyAuthority('"+ UserConstants.DELETE_PERMISSION+"')")
	public void deleteTenant(@PathVariable Long id) {
		log.info("TenantController::deleteTenant  Id {} ",id);
		tenantService.deleteTenant(id);
	}
}
