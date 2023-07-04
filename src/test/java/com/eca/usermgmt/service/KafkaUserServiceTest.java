package com.eca.usermgmt.service;

import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.VendorDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.entity.Owner;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.entity.Vendor;
import com.eca.usermgmt.repository.OwnerRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.repository.VendorRepository;
import com.eca.usermgmt.service.impl.UserUpdateServiceImpl;
import com.eca.usermgmt.service.notifiation.NotificationService;
import com.eca.usermgmt.service.strategy.UserStrategyFactory;
import com.eca.usermgmt.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KafkaUserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ModelMapper modelMapper;

	@Mock
	private UserStrategyFactory userStrategyFactory;

	@InjectMocks
	private JsonUtils jsonUtils;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private VendorRepository vendorRepository;

	@Mock
	private UserService userService;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private UserUpdateServiceImpl updateUserService;

	@InjectMocks
	private ObjectMapper objectMapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private Validator validator;


	@BeforeEach
	void init() {
		when(passwordEncoder.encode(any())).thenReturn("JIwowu");
	}
	@Test
	@SneakyThrows
	void testUpdateOwnerKafka() {
		ReflectionTestUtils.setField(updateUserService,"modelMapper",modelMapper);
		ReflectionTestUtils.setField(updateUserService,"kafkaEnabled",true);
		ReflectionTestUtils.setField(updateUserService,"jsonUtils",jsonUtils);
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/owner_requests.json")),
				StandardCharsets.UTF_8);
		var owner = objectMapper.readValue(json, Owner.class);
		when(ownerRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(owner));
		when(ownerRepository.saveAndFlush(any())).thenReturn(owner);
		var userDetails = getUserDetails();
		userDetails.setOwner(owner);
		when(userService.getOwner(any())).thenReturn(Optional.of(owner));
		when(userService.getUserDetailsByOwnerId(any())).thenReturn(userDetails);
		doNothing().when(userService).saveUserDetails(any());
		ResponseEntity<BaseResponse> response = updateUserService.updateOwner(1L, new OwnerDTO());
		Assertions.assertThat(response).isNotNull();
		verify(notificationService,times(1)).sendNotification(anyString());
	}

	private static User getUserDetails() {
		var userDetails = new User();
		userDetails.setCreatedOn(LocalDateTime.now());
		userDetails.setUpdatedOn(LocalDateTime.now());
		userDetails.setVersion(1L);
		userDetails.setUsername("test@gamilcom");
		userDetails.setPassword("1234344");
		return userDetails;
	}

	@Test
	@SneakyThrows
	void testUpdateVendorKafka() {
		ReflectionTestUtils.setField(updateUserService,"modelMapper",modelMapper);
		ReflectionTestUtils.setField(updateUserService,"kafkaEnabled",true);
		ReflectionTestUtils.setField(updateUserService,"jsonUtils",jsonUtils);
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/vendor_request.json")),
				StandardCharsets.UTF_8);
		var vendorEntity = objectMapper.readValue(json, Vendor.class);
		when(vendorRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(vendorEntity));
		when(vendorRepository.saveAndFlush(any())).thenReturn(vendorEntity);
		var userDetails = getUserDetails();
		userDetails.setVendor(vendorEntity);
		when(userService.getUserDetailsByVendorId(any())).thenReturn(userDetails);
		doNothing().when(userService).saveUserDetails(any());
		when(userService.findVendorWithId(any())).thenReturn(Optional.of(vendorEntity));
		ResponseEntity<BaseResponse> response = updateUserService.updateVendor(1L, new VendorDTO());
		Assertions.assertThat(response).isNotNull();
		verify(notificationService,times(1)).sendNotification(anyString());
	}


}
