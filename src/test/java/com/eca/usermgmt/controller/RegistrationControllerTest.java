package com.eca.usermgmt.controller;

import com.eca.usermgmt.dto.ApartmentDetailsDTO;
import com.eca.usermgmt.dto.request.UserRegistrationRequest;
import com.eca.usermgmt.feign.ApartmentFeignClient;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("dev")
class RegistrationControllerTest {

	public static final String REG_URL_PATH = "/v1/users/registration";
	@Autowired
	private MockMvc mvc;

	@Autowired
	private JsonUtils jsonUtils;

	@Value("${app.application.cache.expiryInSec:10800}")
	private long cacheExpiryInSec;

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
	@Order(1)
	void registerOwnerTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(getUserRegistrationRequest("/owner_requests.json")))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$.data.username").value("owner.test@gmail.com"));

	}

	@Test
	@Order(2)
	void checkOwnerNonUnique() throws Exception {
		var userRegistrationRequest = getUserRegistrationRequest("/owner_requests.json");
		userRegistrationRequest.setEmailId("owner.test@gmail.com");
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.content(jsonUtils.toJson(userRegistrationRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(jsonPath("$.error").hasJsonPath())
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@SneakyThrows
	void checkUnKnownTypeTest() {
		var userRegistrationRequest = getUserRegistrationRequest("/tenants_request.json");
		userRegistrationRequest.setType("UNKNOWN");
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(userRegistrationRequest))
				)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error", is(notNullValue())));
	}

	@Test
	void invalidUserRegisterRequestTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(new UserRegistrationRequest()))
				)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error", is(notNullValue())));
	}

	@Test
	void checkInvalidEmailRegisterWithVendorTest() throws Exception {
		var vendorDTO = getUserRegistrationRequest("/vendor_request.json");
		vendorDTO.setEmailId(null);
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(vendorDTO))
				)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(jsonPath("$.error", is(Matchers.notNullValue())))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	@Order(10)
	void registerVendorTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(getUserRegistrationRequest("/vendor_request.json")))
				)
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$.data.username").value("vendor.test@gmail.com"));
	}

	@Test
	void registerTenantTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(getUserRegistrationRequest("/tenants_request.json")))
				)
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$.data.username").value("tenant-test@gmail.com"));
	}

	@Test
	void checkTenantsWithLeaseStartDateInvalidRequest() throws Exception {
		var userRegistrationRequest = getUserRegistrationRequest("/tenants_request.json");
		userRegistrationRequest.setLeaseStartDate(null);
		userRegistrationRequest.setLeaseEndDate(null);
		mvc.perform(MockMvcRequestBuilders
						.post(REG_URL_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonUtils.toJson(userRegistrationRequest))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andExpect(MockMvcResultMatchers.jsonPath("$.error", is(notNullValue())));

	}


	private UserRegistrationRequest getUserRegistrationRequest(String filePath) throws IOException {
		var json = IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream(filePath)),
				StandardCharsets.UTF_8);
		return jsonUtils.jsonToObject(json, UserRegistrationRequest.class);
	}


}

