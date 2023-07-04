package com.eca.usermgmt.dto.request;

import com.eca.usermgmt.constraint.EmailId;
import com.eca.usermgmt.constraint.PhoneNumber;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @NotNull
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    @PhoneNumber
    private Long phoneNo;

    @JsonProperty("emailId")
    @EmailId
    @NotBlank
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

    @NotBlank
    private String password;

    @NotNull
    @JsonProperty("apartmentId")
    private Long apartmentId;

    private BigDecimal apartmentCost;

    private String leaseStartDate;

    private String leaseEndDate;

    @NotBlank
    private String type;

}
