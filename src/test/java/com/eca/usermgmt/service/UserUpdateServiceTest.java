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
import com.eca.usermgmt.utils.JsonUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("dev")
class UserUpdateServiceTest {
	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private UserUpdateService userUpdateService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private OwnerRepository ownerRepository;

	@MockBean
	private VendorRepository vendorRepository;

	@Test
	@SneakyThrows
	void updateOwnerTest() {
		Owner owner = getOwner();
		User user = new User();
		user.setUsername("db@gmail.com");
		user.setPassword("test@123");
		user.setUserPhoneNumber(9858121523L);
		user.setOwner(owner);
		Mockito.when(userRepository.getUserDetailsByOwnerId(anyLong())).thenReturn(user);
		Mockito.when(ownerRepository.findById(anyLong())).thenReturn(Optional.of(owner));
		Mockito.when(ownerRepository.save(any())).thenReturn(owner);
		Mockito.when(userRepository.save(any())).thenReturn(user);
		var ownerDTO = new OwnerDTO();
		ownerDTO.setEmailId("test@gmail.com");
		ownerDTO.setPassword("jai38972");
		ownerDTO.setPhoneNo(9890348923L);
		ownerDTO.setCity("test");
		ownerDTO.setAddressLine("1");
		ownerDTO.setState("jk");
		ownerDTO.setZipCode("73823");
		ownerDTO.setLastName("LN");
		ownerDTO.setFirstName("FA");
		ResponseEntity<BaseResponse> response = userUpdateService.updateOwner(909L, ownerDTO);
		assertThat(response).isNotNull();
	}

	private Owner getOwner() throws IOException {
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/owner_requests.json")),
				StandardCharsets.UTF_8);
		return jsonUtils.jsonToObject(json, Owner.class);
	}

	@Test
	@SneakyThrows
	void updateVendorTest() {
		var vendor = getVendor();
		User user = new User();
		user.setUsername("db-vendor@gmail.com");
		user.setPassword("test-vendor@123");
		user.setUserPhoneNumber(94728272L);
		user.setVendor(vendor);
		Mockito.when(userRepository.getUserDetailsByVendorId(anyLong())).thenReturn(user);
		Mockito.when(vendorRepository.findById(anyLong())).thenReturn(Optional.of(vendor));
		Mockito.when(vendorRepository.save(any())).thenReturn(vendor);
		Mockito.when(userRepository.save(any())).thenReturn(user);
		var vendorDTO = new VendorDTO();
		vendorDTO.setEmailId("vendor-update@gmail.com");
		vendorDTO.setPassword("update930");
		vendorDTO.setPhoneNo(990728222L);
		vendorDTO.setCity("test");
		vendorDTO.setAddressLine("1");
		vendorDTO.setState("jk");
		vendorDTO.setZipCode("73823");
		vendorDTO.setLastName("LN");
		vendorDTO.setFirstName("FA");
		ResponseEntity<BaseResponse> response = userUpdateService.updateVendor(909L, vendorDTO);
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isNotNull();
	}

	private Vendor getVendor() throws IOException {
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/vendor_request.json")),
				StandardCharsets.UTF_8);
		return jsonUtils.jsonToObject(json, Vendor.class);
	}
}
