package com.eca.usermgmt.controller;

import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.repository.TenantsRepository;
import com.eca.usermgmt.security.CustomUserDetails;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("dev")
class TenantControllerTest {

	public static final String URL_PATH = "/v1/users/tenants";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private TenantsRepository tenantsRepository;

	private String readTenantsJson() throws IOException {
		return IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/tenants_request.json")),
				StandardCharsets.UTF_8);
	}


	@Test
	@Order(3)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void findAllTenants() throws Exception {
		mvc.perform(
				MockMvcRequestBuilders.get(URL_PATH)
						.accept(MediaType.APPLICATION_JSON)
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$.data").isArray());

	}

	@Test
	@SneakyThrows
	@Order(1)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void createTenantsTest() {
		var tenantsDto = jsonUtils.jsonToObject(readTenantsJson(), TenantsDTO.class);
		tenantsDto.setEmailId("robin_singh@gmail.com");
		tenantsDto.setFirstName("ROBIN");
		tenantsDto.setPassword("robinTestPwd");
		mvc.perform(
					post(URL_PATH)
							.contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON)
							.content(jsonUtils.toJson(tenantsDto))
				)
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data.[*].type").value(TypeOfUser.TENANT.toString()))
				.andExpect(jsonPath("$.error").doesNotExist());
	}

	@Test
	@SneakyThrows
	@Order(2)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void updateTest() {
		var tenantsDto = jsonUtils.jsonToObject(readTenantsJson(), TenantsDTO.class);
		tenantsDto.setEmailId("testuser@gmail.com");
		tenantsDto.setFirstName("MITALI");
		tenantsDto.setLastName("GUPTA");
		mvc.perform(
						put(StringUtils.join(URL_PATH,"/4"))
								.contentType(MediaType.APPLICATION_JSON)
								.content(jsonUtils.toJson(tenantsDto))
				)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	@SneakyThrows
	@Order(4)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void deleteTest() {
		mvc.perform(
						delete(StringUtils.join(URL_PATH,"/1"))
								.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}
	@Test
	@Order(5)
	@CustomUserDetails(value = "mitaligupta@gmail.com")
	void findTenantsByIdTest() throws Exception {
		mvc.perform(
						MockMvcRequestBuilders.get(URL_PATH)
								.accept(MediaType.APPLICATION_JSON)
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.data").exists())
				.andExpect(jsonPath("$.data").isArray());

	}
}
