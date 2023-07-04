package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.KafkaMessageDTO;
import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.UserDetailsDTO;
import com.eca.usermgmt.dto.VendorDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.response.UserDetailsResponse;
import com.eca.usermgmt.entity.Owner;
import com.eca.usermgmt.entity.Vendor;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.repository.VendorRepository;
import com.eca.usermgmt.service.PermissionService;
import com.eca.usermgmt.service.RoleService;
import com.eca.usermgmt.service.UserUpdateService;
import com.eca.usermgmt.service.UserService;
import com.eca.usermgmt.service.notifiation.NotificationService;
import com.eca.usermgmt.service.strategy.UserStrategyFactory;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserUpdateServiceImpl implements UserUpdateService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserStrategyFactory userStrategyFactory;

	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired(required = false)
	private NotificationService notificationService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.kafka.enabled}")
	private boolean kafkaEnabled;

	@Autowired
	private UserService userService;

	@Autowired
	private Validator validator;

	/**
	 * Update Owner Type.
	 * @param ownerId OwnerId
	 * @param ownerDTO OwnerRequest
	 * @return Updated Owner.
	 */
	@Override
	@Transactional
	public ResponseEntity<BaseResponse> updateOwner(Long ownerId, OwnerDTO ownerDTO) {
		Optional<Owner> ownerEntity = userService.getOwner(ownerId);
		log.info("UpdateUserServiceImpl::updateOwner get Owner From DB {}",ownerEntity);
		return ownerEntity
				.map(owner -> {
					prepareOwner(ownerDTO,owner);
					Set<ConstraintViolation<Owner>> validate = validator.validate(owner);
					if (!validate.isEmpty()) {
						log.error("UpdateUserServiceImpl::updateOwner ConstraintViolationException {} ",validate);
						throw new ConstraintViolationException(validate);
					}
					ownerRepository.saveAndFlush(owner);
					var userDetails = userService.getUserDetailsByOwnerId(owner.getId());
					userDetails.setUserPhoneNumber(owner.getPhoneNo());
					userDetails.setPassword(passwordEncoder.encode(ownerDTO.getPassword()));
					userService.saveUserDetails(userDetails);
					log.info("UpdateUserServiceImpl:: userDetails {} ",userDetails);
					var userDetailsDTO = modelMapper.map(userDetails, UserDetailsDTO.class);
					pushNotification(userDetailsDTO);
					return toBaseResponse(List.of(userDetailsDTO));
				}).orElseThrow(() -> new UserManagementException(UserConstants.OWNER_UPDATION_ERROR+ownerId));
	}

	/**
	 * Update Vendor Type
	 * @param vendorId Vendor Id.
	 * @param vendorDTO Vendor Request
	 * @return Updated Vendor.
	 */
	@Override
	@Transactional
	public ResponseEntity<BaseResponse> updateVendor(Long vendorId, VendorDTO vendorDTO) {
		Optional<Vendor> vendorEntity = Optional.of(userService.findVendorWithId(vendorId))
				.filter(Optional::isPresent)
				.orElseThrow(() -> new UserManagementException(UserConstants.VENDOR_ID_IS_NOT_FOUND, vendorId.toString()));
		log.info("UpdateUserServiceImpl::updateVendor vendorEntity {}",vendorEntity);
		var userDetailsDTO = vendorEntity
				.map(vendor -> {
					prepareVendorEntity(vendorDTO, vendor);
					Set<ConstraintViolation<Vendor>> validate = validator.validate(vendor);
					if (!validate.isEmpty()) {
						log.error("UpdateUserServiceImpl::updateVendor ConstraintViolationException {} ",validate);
						throw new ConstraintViolationException(validate);
					}
					vendorRepository.saveAndFlush(vendor);
					var userDetails = userService.getUserDetailsByVendorId(vendor.getId());
					userDetails.setUserPhoneNumber(vendor.getPhoneNo());
					userDetails.setPassword(passwordEncoder.encode(vendorDTO.getPassword()));
					userService.saveUserDetails(userDetails);
					var vendorDetails = modelMapper.map(userDetails, UserDetailsDTO.class);
					pushNotification(vendorDetails);
					return vendorDetails;
				}).orElseThrow(() -> new UserManagementException(UserConstants.VENDOR_UPDATION_ERROR + vendorId));
		return toBaseResponse(List.of(userDetailsDTO));

	}


	private void prepareVendorEntity(VendorDTO vendorDTO, Vendor vendor) {
		if (StringUtils.isNotBlank(vendorDTO.getFirstName())) {
			vendor.setFirstName(vendorDTO.getFirstName());
		}
		if (StringUtils.isNotBlank(vendorDTO.getLastName())) {
			vendor.setLastName(vendorDTO.getLastName());
		}
		if (StringUtils.isNotBlank(vendorDTO.getAddressLine())) {
			vendor.setAddressLine(vendorDTO.getAddressLine());
		}
		if (StringUtils.isNotBlank(vendorDTO.getCity())) {
			vendor.setCity(vendorDTO.getCity());
		}
		if (StringUtils.isNotBlank(vendorDTO.getState())) {
			vendor.setState(vendorDTO.getState());
		}
		if (StringUtils.isNotBlank(vendorDTO.getZipCode())) {
			vendor.setZipCode(vendorDTO.getZipCode());
		}
		vendor.setType(TypeOfUser.VENDOR.toString());
	}

	private void pushNotification(UserDetailsDTO details) {
		if (kafkaEnabled) {
			var kafkaMessageDTO = KafkaMessageDTO.builder()
					.userId(details.getUsername())
					.build();
			buildVendorOwnerTenant(kafkaMessageDTO,details);
			kafkaMessageDTO.setEventType(KafkaMessageDTO.EventType.UPDATE);
			notificationService.sendNotification(jsonUtils.toJson(kafkaMessageDTO));
		}
	}

	private void buildVendorOwnerTenant(KafkaMessageDTO kafkaMessage,UserDetailsDTO userDetails) {
		if(userDetails.getTenant() != null) {
			kafkaMessage.setTenant(modelMapper.map(userDetails.getTenant(), TenantsDTO.class));
			kafkaMessage.getTenant().setId(null);
		} else if( userDetails.getVendor() !=null ) {
			kafkaMessage.setVendor(jsonUtils.convertObject(userDetails.getVendor(),VendorDTO.class));
			kafkaMessage.getVendor().setId(null);
		} else if (userDetails.getOwner() !=null) {
			kafkaMessage.setOwner(modelMapper.map(userDetails.getOwner(),OwnerDTO.class));
			kafkaMessage.getOwner().setId(null);
		}
	}

	private ResponseEntity<BaseResponse> toBaseResponse(List<UserDetailsDTO> userDetailsList) {
		var buildResponse = new UserDetailsResponse();
		buildResponse.setTimestamp(LocalDateTime.now());
		if (userDetailsList !=null && !userDetailsList.isEmpty()) {
			buildResponse.setUserDetails(userDetailsList);
		} else {
			buildResponse.setError("Not Found");
		}
		return new ResponseEntity<>(buildResponse, HttpStatus.ACCEPTED);
	}

	private void prepareOwner(OwnerDTO ownerDTO, Owner owner) {
		if (StringUtils.isNotBlank(ownerDTO.getFirstName())) {
			owner.setFirstName(ownerDTO.getFirstName());
		}
		if (StringUtils.isNotBlank(ownerDTO.getLastName())) {
			owner.setLastName(ownerDTO.getLastName());
		}
		if (ownerDTO.getPhoneNo() !=null) {
			owner.setPhoneNo(ownerDTO.getPhoneNo());
		}
		if (StringUtils.isNotBlank(ownerDTO.getAddressLine())) {
			owner.setAddressLine(ownerDTO.getAddressLine());
		}
		if (StringUtils.isNotBlank(ownerDTO.getCity())) {
			owner.setCity(ownerDTO.getCity());
		}
		if (StringUtils.isNotBlank(ownerDTO.getState())) {
			owner.setState(ownerDTO.getState());
		}
		if (StringUtils.isNotBlank(ownerDTO.getZipCode())) {
			owner.setZipCode(ownerDTO.getZipCode());
		}
		owner.setType(TypeOfUser.OWNER.toString());
	}


}
