package com.eca.usermgmt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageDTO {
	public enum EventType {
		CREATE,UPDATE,DELETE
	}

	private EventType eventType;

	private String userId;

	private TenantsDTO tenant;

	private VendorDTO vendor;

	private OwnerDTO owner;

}
