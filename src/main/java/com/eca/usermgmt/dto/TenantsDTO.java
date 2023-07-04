package com.eca.usermgmt.dto;

import com.eca.usermgmt.constants.UserConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@JsonTypeName(UserConstants.TYPE_TENANT)
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantsDTO extends UserCommonInfoDTO {

    private Long id;

    private BigDecimal apartmentCost;

    @NotNull
    private String leaseStartDate;

    @NotNull
    private String leaseEndDate;

}
