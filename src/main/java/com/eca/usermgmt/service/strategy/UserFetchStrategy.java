package com.eca.usermgmt.service.strategy;

import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.UserDetailsDTO;
import com.eca.usermgmt.dto.response.UserDetailsResponse;
import com.eca.usermgmt.enums.TypeOfUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface UserFetchStrategy {
	ResponseEntity<BaseResponse> getListOfUser();
	TypeOfUser strategyName();

	default ResponseEntity<BaseResponse> toBaseResponse(List<UserDetailsDTO> userDetailsDTOS) {
		var buildResponse = new UserDetailsResponse();
		buildResponse.setTimestamp(LocalDateTime.now());
		buildResponse.setUserDetails(userDetailsDTOS);
		return new ResponseEntity<>(buildResponse, HttpStatus.OK);
	}
}
