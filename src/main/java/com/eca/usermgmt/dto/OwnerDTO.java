package com.eca.usermgmt.dto;

import com.eca.usermgmt.constants.UserConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeName(UserConstants.TYPE_OWNER)
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerDTO extends UserCommonInfoDTO {

	private Long id;
}
