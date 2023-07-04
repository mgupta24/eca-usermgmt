package com.eca.usermgmt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Builder
public class UserDetailsDTO {

	private long id;

	private String username;

	private OwnerDTO owner;

	private TenantsDTO tenant;

	private VendorDTO vendor;

	private Long userPhoneNumber;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;
}
