package com.eca.usermgmt.dto.response;


import com.eca.usermgmt.dto.OwnerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerResponse extends BaseResponse {

	@JsonProperty("data")
	private List<OwnerDTO> ownerData;
}
