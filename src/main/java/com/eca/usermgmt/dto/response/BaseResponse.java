package com.eca.usermgmt.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseResponse {

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	protected LocalDateTime timestamp;

	protected String error;
}
