package com.eca.usermgmt.controller;


import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.VendorDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.response.UserDetailsResponse;
import com.eca.usermgmt.service.UserService;
import com.eca.usermgmt.service.UserUpdateService;
import com.eca.usermgmt.service.strategy.UserStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Validator;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/users")
@Slf4j
@CrossOrigin
public class UserDetailsController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserStrategyFactory userFetchStrategy;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserUpdateService userUpdateService;

	@Autowired
	private Validator validator;

	@GetMapping("/getAllUsers")
	@PreAuthorize("hasAnyAuthority('"+UserConstants.WRITE_PERMISSION+"','"+UserConstants.AUTHORITY_OWNER+"')")
	public ResponseEntity<BaseResponse> getUserDetails(@RequestParam(value = UserConstants.TYPE, defaultValue = UserConstants.ALL)
	                                                       String typeOfUser) {
		log.info("UserDetailsController::getUserDetails {}",typeOfUser);
		return userService.getAllUsers(typeOfUser);
	}

	@GetMapping("/phone/{phoneNumber}")
	@PreAuthorize("hasPermission(#phoneNumber, '" +UserConstants.READ_PERMISSION+"')")
	public ResponseEntity<BaseResponse> getUserByPhoneNumber(@PathVariable("phoneNumber") Long phoneNumber) {
		log.info("UserDetailsController::getUserByPhoneNumber {}",phoneNumber);
		if(phoneNumber !=null) {
			return userService.getUserByPhoneNumber(phoneNumber);
		} else {
			var buildResponse = new UserDetailsResponse();
			buildResponse.setTimestamp(LocalDateTime.now());
			buildResponse.setError("phone number should not be null or empty");
			log.error("phone number should not be null or empty");
			return new ResponseEntity<>(buildResponse,  HttpStatus.NOT_FOUND);
		}

	}

	/**
	 * Update Owner Not Updating EmailId, Because emailId is Username For LoggedIn Flow.
	 * @param ownerId ownerId
	 * @param ownerDTO OwnerDTO
	 * @return BaseResponse.
	 */
	@PutMapping("/owner/{ownerId}")
	@PreAuthorize("hasAnyAuthority('WRITE_PERMISSION')")
	public ResponseEntity<BaseResponse> updateOwner(@PathVariable("ownerId") Long ownerId , @RequestBody OwnerDTO ownerDTO) {
		log.info("UserDetailsController::updateOwner  ownerId {} OwnerDto {}",ownerId,ownerDTO);
		return userUpdateService.updateOwner(ownerId,ownerDTO);
	}

	/**
	 * Update Vendor Not Updating EmailId, Because emailId is Username For LoggedIn Flow.
	 * @param vendorId vendorId
	 * @param vendorDTO VendorDTO
	 * @return BaseResponse.
	 */
	@PutMapping("/vendor/{vendorId}")
	@PreAuthorize("hasAnyAuthority('"+UserConstants.UPDATE_PERMISSION+"','"+UserConstants.WRITE_PERMISSION+"','"+UserConstants.VENDOR_PERMISSION+"')")
	public ResponseEntity<BaseResponse> updateVendor(@PathVariable("vendorId") Long vendorId ,
	                                            @RequestBody VendorDTO vendorDTO) {
		log.info("UserDetailsController::updateOwner  Vendor Id :: {} Vendor Request:: {}",vendorId,vendorDTO);
		return userUpdateService.updateVendor(vendorId,vendorDTO);
	}
}
