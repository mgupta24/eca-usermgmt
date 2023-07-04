package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.dto.UserDetailsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.response.UserDetailsResponse;
import com.eca.usermgmt.entity.Owner;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.entity.Vendor;
import com.eca.usermgmt.enums.Permissions;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.exception.UserRegistrationFailed;
import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.repository.VendorRepository;
import com.eca.usermgmt.service.PermissionService;
import com.eca.usermgmt.service.RoleService;
import com.eca.usermgmt.service.UserService;
import com.eca.usermgmt.service.strategy.UserStrategyFactory;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.eca.usermgmt.constants.UserConstants.ROLE_OWNER;
import static com.eca.usermgmt.constants.UserConstants.ROLE_TENANT;
import static com.eca.usermgmt.constants.UserConstants.ROLE_VENDOR;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserStrategyFactory userStrategyFactory;

	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Override
	public User saveUserWithDefaultRoles(User user) {
		saveRoles(user);
		settingUserEnable(user);
		var save = userRepository.save(user);
		log.info("UserServiceImpl::saveUserWithDefaultRoles {}",user);
		return Optional.of(save)
				.orElseThrow(() -> new UserRegistrationFailed("User RegistrationFailed"));
	}

	/**
	 * Find All Users Owner/Tenant/Vendor
	 * @param typeOfUser Which TypeOf User
	 * @return All Users
	 */
	@Override
	public ResponseEntity<BaseResponse> getAllUsers(String typeOfUser) {
		var strategy = userStrategyFactory.findStrategy(TypeOfUser.getUserType(typeOfUser));
		log.info("UserService getAllUsers:: TypeOfUser {}  Response {} ",typeOfUser,strategy);
		return strategy;
	}

	/**
	 * Find User With Phone Number (Tenant/Owner/Vendor)
	 * @param phoneNumber Valid Phone Number
	 * @return User With PhoneNumber Requested.
	 */
	@Override
	public ResponseEntity<BaseResponse> getUserByPhoneNumber(Long phoneNumber) {
		var byUserPhoneNumber = userRepository.findByUserPhoneNumber(phoneNumber);
		log.info("UserServiceImpl::getUserByPhoneNumber  response {}",byUserPhoneNumber);
		return Optional.ofNullable(byUserPhoneNumber)
				.map(userDetails -> toBaseResponse(List.of(modelMapper.map(userDetails, UserDetailsDTO.class)),
						HttpStatus.OK))
				.orElse(toBaseResponse(List.of(),HttpStatus.NOT_FOUND));
	}

	/**
	 * Get UserDetails With OwnerId
	 * @param ownerId OwnerId
	 * @return Owner Type User
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserDetailsByOwnerId(Long ownerId) {
		var userDetailsByOwnerId = userRepository.getUserDetailsByOwnerId(ownerId);
		log.info("UserServiceImpl::getUserDetailsByOwnerId  ownerId {} user details by ownerId {} ",ownerId,
				userDetailsByOwnerId);
		return userDetailsByOwnerId;
	}

	/**
	 * Get User Details By VendorId.
	 * @param vendorId Vendor id
	 * @return Vendor Type Of User.
	 */
	@Override
	@Transactional(readOnly = true)
	public User getUserDetailsByVendorId(Long vendorId) {
		var userDetailsByVendorId = userRepository.getUserDetailsByVendorId(vendorId);
		log.info("UserServiceImpl::getUserDetailsByVendorId vendor id {} {}",vendorId,userDetailsByVendorId);
		return userDetailsByVendorId;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Vendor> findVendorWithId(Long vendorId) {
		Optional<Vendor> byId = vendorRepository.findById(vendorId);
		log.info("UserServiceImpl::findVendorWithId vendor Id{} findVendor With Id {}",vendorId,byId);
		return byId;
	}

	@Override
	@Transactional
	public void saveUserDetails(User user) {
		log.info("UserServiceImpl::saveUserDetails User Request {} ",user);
		userRepository.saveAndFlush(user);
	}

	/**
	 * Get Owner With OwnerId
	 * @param ownerId OwnerId
	 * @return Owner or Else Owner Not Found
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<Owner> getOwner(Long ownerId) {
		return Optional.of(ownerRepository.findById(ownerId))
				.filter(Optional::isPresent)
				.orElseThrow(() -> new UserManagementException("OwnerId Not Found ", ownerId.toString()));
	}

	private void settingUserEnable(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(true);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
	}

	private void saveRoles(User user) {
		var readPermission = permissionService.createPermissionIfNotFound(Permissions.READ_PERMISSION.name());
		var writePermission = permissionService.createPermissionIfNotFound(Permissions.WRITE_PERMISSION.name());
		var updatePermission = permissionService.createPermissionIfNotFound(Permissions.UPDATE_PERMISSION.name());
		var deletePermission = permissionService.createPermissionIfNotFound(Permissions.DELETE_PERMISSION.name());
		var vendorPermission = permissionService.createPermissionIfNotFound("VENDOR_PERMISSION");
		if (user.getOwner() !=null) {
			roleService.createIfNotFound(ROLE_OWNER,List.of(readPermission,writePermission,deletePermission,updatePermission));
			var role = roleService.getRoleByName(ROLE_OWNER);
			user.setRoles(Set.of(role));
		} else if (user.getTenant() !=null) {
			roleService.createIfNotFound(ROLE_TENANT,List.of(readPermission,updatePermission));
			var role = roleService.getRoleByName(ROLE_TENANT);
			user.setRoles(Set.of(role));
		} else if (user.getVendor() !=null) {
			roleService.createIfNotFound(ROLE_VENDOR,List.of(readPermission,vendorPermission));
			var role = roleService.getRoleByName(ROLE_VENDOR);
			user.setRoles(Set.of(role));
		}
	}

	private ResponseEntity<BaseResponse> toBaseResponse(List<UserDetailsDTO> userDetailsList, HttpStatus status) {
		var buildResponse = new UserDetailsResponse();
		buildResponse.setTimestamp(LocalDateTime.now());
		if (userDetailsList !=null && !userDetailsList.isEmpty()) {
			buildResponse.setUserDetails(userDetailsList);
		} else {
			buildResponse.setError("Not Found");
		}
		return new ResponseEntity<>(buildResponse, status);
	}
}
