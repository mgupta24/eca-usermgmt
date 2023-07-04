package com.eca.usermgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApartmentDetailsDTO {

	@JsonProperty("name")
	private String apartmentName;

	private Long apartmentId;
}
