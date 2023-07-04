package com.eca.usermgmt.service.strategy;

import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.UserManagementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class UserStrategyFactory {
	private Map<TypeOfUser,UserFetchStrategy> strategyMap;

	public UserStrategyFactory(Map<TypeOfUser, UserFetchStrategy> strategyMap) {
		this.strategyMap = strategyMap;
	}

	public ResponseEntity<BaseResponse> findStrategy(TypeOfUser typeOfUser) {
		log.info("UserStrategyFactory::findStrategy {} ",typeOfUser);
		if(!strategyMap.containsKey(typeOfUser)) {
			throw new UserManagementException("User Type Not Found "+typeOfUser.toString());
		}
		return strategyMap.get(typeOfUser).getListOfUser();
	}
}
