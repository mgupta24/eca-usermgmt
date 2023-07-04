package com.eca.usermgmt.constants;

import org.apache.commons.lang3.StringUtils;

public class UserConstants {

	private UserConstants() {
	}

	public static final String TYPE_OWNER = "OWNER";
	public static final String TYPE_TENANT = "TENANT";
	public static final String TYPE_VENDOR = "VENDOR";
	public static final String TYPE = "type";
	public static final String ALL = "All";
	public static final String OWNER_UPDATION_ERROR = "Owner Updation Error ";
	public static final String VENDOR_UPDATION_ERROR = "Vendor Updation Error ";
	public static final String VENDOR_ID_IS_NOT_FOUND = "Vendor Id Is  Not Found ";
	public static final String AUTHORITIES = "authorities";
	public static final String USERNAME = "username";
	public static final String AUTHORITY = "authority";

	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String PASSWORD_ENCRYPTED = "$2a$10$JCKSC6A2kuaQVAYYzslGH.XVS3EaSwE7slnfpHklfc2S18MHiUt/O";

	public static final String INVALID_USER_TYPE = StringUtils.join("Invalid user type it should be : ", TYPE_VENDOR, "|", TYPE_OWNER, "|", TYPE_TENANT);
    public static final String JSON_PROCESSING_ERROR = "Invalid Json Type JsonProcessing Failed ";
    public static final String USER_REGISTRATION_ERROR = "User RegistrationFailed ";
	public static final String TENANT_CREATION_FAILED = "Tenant Creation Failed ";
	public static final String TENANT_UPDATION_FAILED = "Tenant Updation Is Failed ";

	public static final String  ROLE_OWNER = "ROLE_OWNER";
	public static final String ROLE_VENDOR= "ROLE_VENDOR";
	public static final String ROLE_TENANT = "ROLE_TENANT";

	public static final String READ_PERMISSION = "READ_PERMISSION" ;
	public static final String WRITE_PERMISSION = "WRITE_PERMISSION";
	public static final String DELETE_PERMISSION = "DELETE_PERMISSION";
	public static final String UPDATE_PERMISSION = "UPDATE_PERMISSION";
	public static final String VENDOR_PERMISSION = "VENDOR_PERMISSION";
	public static final String AUTHORITY_OWNER = "OWNER";

}
