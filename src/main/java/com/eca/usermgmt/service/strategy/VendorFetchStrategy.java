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
public class VendorFetchStrategy implements UserFetchStrategy {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ResponseEntity<BaseResponse> getListOfUser() {
		var vendorTypeList = userRepository.getAllVendorByType(TypeOfUser.VENDOR.toString());
		log.info("VendorFetchStrategy::getListOfUser {} ",vendorTypeList);
		return toBaseResponse(buildVendorResponse(vendorTypeList));
	}

	private List<UserDetailsDTO> buildVendorResponse(List<User> vendorTypeList) {
		return Optional.of(vendorTypeList)
				.orElse(Collections.emptyList())
				.stream()
				.filter(Objects::nonNull)
				.map(userDetails -> modelMapper.map(userDetails, UserDetailsDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public TypeOfUser strategyName() {
		return TypeOfUser.VENDOR;
	}
}
