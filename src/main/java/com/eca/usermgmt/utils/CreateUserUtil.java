package com.eca.usermgmt.utils;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.Permissions;
import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.service.PermissionService;
import com.eca.usermgmt.service.RoleService;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class CreateUserUtil {

	private CreateUserUtil() {

	}

	public static void settingDefaultUserForTesting(UserRepository userService, JsonUtils jsonUtils,
	                                                 RoleService roleService, OwnerRepository ownerRepository,
	                                                 PermissionService permissionService) {
		boolean present = (long) Optional.of(userService.findAll())
				.orElse(Collections.emptyList())
				.size() > 0;
		if(!present) {
			var read = permissionService.createPermissionIfNotFound(Permissions.READ_PERMISSION.name());
			var write = permissionService.createPermissionIfNotFound(Permissions.WRITE_PERMISSION.name());
			var update = permissionService.createPermissionIfNotFound(Permissions.UPDATE_PERMISSION.name());
			var delete = permissionService.createPermissionIfNotFound(Permissions.DELETE_PERMISSION.name());
			var user = getUser(jsonUtils);
			user.setPassword(UserConstants.PASSWORD_ENCRYPTED);
			log.info("{}", user.getPassword());
			roleService.createIfNotFound("ROLE_OWNER",List.of(read,write,delete,update));
			var saveRole = roleService.getRoleByName("ROLE_OWNER");
			user.setRoles(Set.of(saveRole));
			var owner = user.getOwner();
			owner.setApartmentId(1L);
			owner.setApartmentName("TEST");
			var own = ownerRepository.save(owner);
			user.setOwner(own);
			var userDetails = userService.save(user);
			log.info("User Details {} ",userDetails);
		}
	}

	private static User getUser(JsonUtils jsonUtils) {
		return jsonUtils.jsonToObject("{\n" +
				"  \"username\": \"mitaligupta@gmail.com\",\n" +
				"  \"password\": \"$2a$10$/QnTOj3aw3LtOpq1XtVrIuEn0Mt/JZejf6FTDWK.bcROMRjdc2uyi\",\n" +
				"  \"owner\": {\n" +
				"    \"type\": \"OWNER\",\n" +
				"    \"firstName\": \"mitali\",\n" +
				"    \"lastName\": \"gupta\",\n" +
				"    \"phoneNo\": 9590319707,\n" +
				"    \"emailId\": \"mitaligupta@gmail.com\",\n" +
				"    \"addressLine\": \"Kathua Marheen\",\n" +
				"    \"city\": \"Kathua \",\n" +
				"    \"state\": \"J&K\",\n" +
				"    \"zipCode\": \"184292\",\n" +
				"    \"password\": \"$2a$10$/QnTOj3aw3LtOpq1XtVrIuEn0Mt/JZejf6FTDWK.bcROMRjdc2uyi\"\n" +
				"  },\n" +
				"  \"userPhoneNumber\": 9590319707,\n" +
				"  \"accountNonExpired\": true,\n" +
				"  \"accountNonLocked\": true,\n" +
				"  \"credentialsNonExpired\": true,\n" +
				"  \"enabled\": true\n" +
				"}\n", User.class);
	}

}
