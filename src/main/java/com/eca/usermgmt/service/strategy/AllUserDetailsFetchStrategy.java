package com.eca.usermgmt.service.strategy;

import com.eca.usermgmt.dto.UserDetailsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AllUserDetailsFetchStrategy implements UserFetchStrategy{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;
	@Override
	public ResponseEntity<BaseResponse> getListOfUser() {
		var listOfResponse = userRepository.findAll();
		log.info("AllUserDetailsFetchStrategy::getListOfUser {} ",listOfResponse);
		var collect = Optional.of(listOfResponse)
				.orElse(Collections.emptyList())
				.stream()
				.map(userDetails -> modelMapper.map(userDetails, UserDetailsDTO.class))
				.collect(Collectors.toList());
		return toBaseResponse(collect);
	}

	@Override
	public TypeOfUser strategyName() {
		return TypeOfUser.ALL;
	}
}
