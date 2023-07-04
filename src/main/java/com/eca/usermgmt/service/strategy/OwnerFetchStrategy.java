package com.eca.usermgmt.service.strategy;

import com.eca.usermgmt.dto.UserDetailsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OwnerFetchStrategy implements UserFetchStrategy{
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Override
	public ResponseEntity<BaseResponse> getListOfUser() {
		var findAllUsers = userRepository.getAllOwnerByType(strategyName().toString());
		log.info("OwnerFetchStrategy::getListOfUser {} ",findAllUsers);
		return toBaseResponse(buildResponse(findAllUsers));
	}

	private List<UserDetailsDTO> buildResponse(List<User> findAllUsers) {
		return Optional.of(findAllUsers)
				.orElse(Collections.emptyList())
				.stream()
				.filter(Objects::nonNull)
				.map(userDetails -> modelMapper.map(userDetails, UserDetailsDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public TypeOfUser strategyName() {
		return TypeOfUser.OWNER;
	}
}
