package com.eca.usermgmt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class UserLoginRequest {

	@NotNull
	@Valid
	private String username;

	@NotNull
	private String password;
}
