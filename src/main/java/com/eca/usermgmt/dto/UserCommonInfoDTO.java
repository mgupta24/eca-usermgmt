package com.eca.usermgmt.dto;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.constraint.EmailId;
import com.eca.usermgmt.constraint.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OwnerDTO.class, name = UserConstants.TYPE_OWNER),
        @JsonSubTypes.Type(value = TenantsDTO.class, name = UserConstants.TYPE_TENANT),
        @JsonSubTypes.Type(value = VendorDTO.class, name = UserConstants.TYPE_VENDOR)
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCommonInfoDTO {

    @NotNull
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    @PhoneNumber
    private Long phoneNo;

    @EmailId
    private String emailId;

    @NotBlank
    private String addressLine;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotNull
    @NotEmpty
    private String zipCode;

    private String password;

    @NotNull
    private Long apartmentId;

    private String apartmentName;
}
