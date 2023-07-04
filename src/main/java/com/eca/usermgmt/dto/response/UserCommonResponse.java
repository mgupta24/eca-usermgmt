package com.eca.usermgmt.dto.response;

import com.eca.usermgmt.dto.UserRegistrationResponseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCommonResponse extends BaseResponse {

	@JsonProperty("data")
	private UserRegistrationResponseDTO responseDto;
}
