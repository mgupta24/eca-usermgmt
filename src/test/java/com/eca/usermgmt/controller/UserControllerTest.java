package com.eca.usermgmt.controller;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.ApartmentDetailsDTO;
import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.request.UserLoginRequest;
import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.feign.ApartmentFeignClient;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.security.CustomUserDetails;
import com.eca.usermgmt.service.RegistrationService;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("dev")
class UserControllerTest {

	public static final String API_APARTMENT = "/v1/users/";
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private UserRepository userRepository;

	@MockBean
	private ApartmentFeignClient apartmentFeignClient;

	@BeforeEach
	void init() {
		var apartmentDetailsDto = new ApartmentDetailsDTO();
		apartmentDetailsDto.setApartmentId(1L);
		apartmentDetailsDto.setApartmentName("TEST_APARTMENT");
		Mockito.when(apartmentFeignClient.getApartmentById(Mockito.any())).thenReturn(Optional.of(apartmentDetailsDto));
	}

	@Test
	@SneakyThrows
	@Order(1)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void getAllUsersNoTest() {
		mockMvc.perform(
						MockMvcRequestBuilders.get(API_APARTMENT + "getAllUsers")
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(200));
	}

	private UserRegistrationRequest getUserRegistrationRequest(String filePath) throws IOException {
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream(filePath)),
				StandardCharsets.UTF_8);
		return jsonUtils.jsonToObject(json, UserRegistrationRequest.class);
	}

	@SneakyThrows
	@Test
	@Order(2)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void getAllUsersWithLoginFlowTest() {
		Optional.of(registrationService.registration(getUserRegistrationRequest("/tenants_request.json")))
				.ifPresent(customResponseResponseEntity -> {
					System.out.println("registrationService = " + customResponseResponseEntity);
					try {
						mockMvc.perform(
										MockMvcRequestBuilders.get(API_APARTMENT + "getAllUsers")
												.param(UserConstants.TYPE, TypeOfUser.TENANT.toString())
												.accept(MediaType.APPLICATION_JSON)
								)
								.andDo(print())
								.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
								.andExpect(jsonPath("$.data").isNotEmpty())
								.andExpect(jsonPath("$.data.[*].username").value("tenant-test@gmail.com"))
								.andExpect(jsonPath("$.data.[*].tenant.type").value("TENANT"));
						loginFlow();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	@SneakyThrows
	void loginFlow() {
		var userLoginRequest = new UserLoginRequest();
		userLoginRequest.setUsername("tenant-test@gmail.com");
		userLoginRequest.setPassword("tenant@123");
		mockMvc.perform(
						MockMvcRequestBuilders.post(API_APARTMENT + "login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonUtils.toJson(userLoginRequest))
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}

	@Test
	@SneakyThrows
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void invalidUserTest() {
		UserLoginRequest userLoginRequest = new UserLoginRequest();
		mockMvc.perform(
						MockMvcRequestBuilders.post(API_APARTMENT + "login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonUtils.toJson(userLoginRequest))
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(jsonPath("$.data").doesNotExist());
		userLoginRequest.setUsername("Owner.Singh@gmail.com");
		mockMvc.perform(
						MockMvcRequestBuilders.post(API_APARTMENT + "login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonUtils.toJson(userLoginRequest))
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(jsonPath("$.data").doesNotExist());

	}

	@Test
	@Order(3)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void checkOwnerStrategyTest() throws Exception {
		Optional.ofNullable(registrationService.registration(getUserRegistrationRequest("/owner_requests.json"))).ifPresent(data -> {
			try {
				mockMvc.perform(MockMvcRequestBuilders
								.get(API_APARTMENT + "getAllUsers")
								.param(UserConstants.TYPE, TypeOfUser.OWNER.toString())
								.accept(MediaType.APPLICATION_JSON))
						.andDo(print())
						.andExpect(MockMvcResultMatchers.status().is(200))
						.andExpect(jsonPath("$.data.[*].username").exists());
			} catch (Exception ignored) {
			}
		});
	}

	@Test
	void checkPhoneNumber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
						.get(API_APARTMENT + "phone/9590319707")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().is(403));
	}

	@Test
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void checkPhoneNumberNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
						.get(API_APARTMENT + "phone/2390892")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(404));
	}

	@SneakyThrows
	@Test
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void testVendorFlowTest() {
		Optional.of(registrationService.registration(getUserRegistrationRequest("/vendor_request.json")))
				.ifPresent(vendorResponse -> {
					assertThat(vendorResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
					try {
						mockMvc.perform(
										MockMvcRequestBuilders.get(API_APARTMENT + "getAllUsers")
												.param(UserConstants.TYPE, TypeOfUser.VENDOR.toString())
												.accept(MediaType.APPLICATION_JSON)
								)
								.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
								.andExpect(jsonPath("$.data").isNotEmpty())
								.andExpect(jsonPath("$.data.[*].username").value("vendor.test@gmail.com"))
								.andExpect(jsonPath("$.data.[*].vendor.type").value("VENDOR"));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	@SneakyThrows
	@Test
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void updateOwnerTest() {
		var ownerDTO = new OwnerDTO();
		ownerDTO.setEmailId("updatedemailId@testcom");
		ownerDTO.setPassword("test@123Updated");
		mockMvc.perform(
						MockMvcRequestBuilders.put(API_APARTMENT + "owner/909")
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonUtils.toJson(ownerDTO))
								.accept(MediaType.APPLICATION_JSON)
				)
				.andExpect(MockMvcResultMatchers.status().is(404));

	}

	@Test
	@Order(3)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void updateOwnerRequestTest() throws Exception {
		var userRegistrationRequest = getUserRegistrationRequest("/owner_requests.json");
		userRegistrationRequest.setPhoneNo(3434343434L);
		userRegistrationRequest.setEmailId("apnl@gmail.com");
		Optional.ofNullable(registrationService.registration(userRegistrationRequest)).ifPresent(data -> {
				Optional<User> byEmailId = userRepository.findByUsername("owner.test@gmail.com");
				byEmailId.ifPresent(user -> {
					userRegistrationRequest.setEmailId("updated-test-owner@gmail.com");
					userRegistrationRequest.setPassword("updatedPwd");
					userRegistrationRequest.setPhoneNo(8234823490L);
					try {
					mockMvc.perform(MockMvcRequestBuilders
									.put(API_APARTMENT + StringUtils.join("owner","/",user.getOwner().getId()))
									.content(jsonUtils.toJson(userRegistrationRequest))
									.contentType(MediaType.APPLICATION_JSON)
									.accept(MediaType.APPLICATION_JSON))
							.andDo(print())
							.andExpect(MockMvcResultMatchers.status().isAccepted())
							.andExpect(jsonPath("$.data.[*].username").value("updated-test-owner@gmail.com"));
					} catch (Exception ignored) {
					}
				});
		});
	}

}
