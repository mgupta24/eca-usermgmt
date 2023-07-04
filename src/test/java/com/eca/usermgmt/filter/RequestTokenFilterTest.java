package com.eca.usermgmt.filter;

import com.eca.usermgmt.cache.CacheService;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.request.UserLoginRequest;
import com.eca.usermgmt.dto.VendorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
class RequestTokenFilterTest {

	private static final String V1_USERS_URL_PATH = "/v1/users/";
	@Autowired
	private TestRestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CacheService cacheService;

	@BeforeEach
	void init() {
		when(cacheService.getFromCache(any())).thenReturn(new TenantsDTO());
		doNothing().when(cacheService).addToCache(eq("TENANT_1"),any(),eq(1), eq(TimeUnit.MINUTES));
	}

	@Test
	@Order(1)
	void generateTokenTest() {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<UserLoginRequest> entity = new HttpEntity<>(
				UserLoginRequest.of("mitaligupta@gmail.com", "mitali"),headers);
		var token = restTemplate.postForObject(V1_USERS_URL_PATH + "login", entity, String.class);
		log.info("Token {} ",token);
		JsonNode root;
		try {
			root = objectMapper.readTree(token);
			JsonNode tokenVal = root.path("token");
			log.info("{}",tokenVal.asText());
			assertThat(tokenVal.asText()).isNotNull();
			System.setProperty("token",tokenVal.asText());
			Assert.isTrue(tokenVal.asText()!=null, "Token received successfully");
		} catch (JsonProcessingException e) {
			Assert.isTrue(false,"Exception occurred: "+e.getMessage());
		}
	}

	@Order(2)
	@Test
	 void checkRequestIsValidOrNot() {
		var httpHeaders = getHttpHeaders();
		ResponseEntity<String> exchange = restTemplate.exchange(V1_USERS_URL_PATH + "getAllUsers", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
		assertThat(exchange).isNotNull();
		assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private HttpHeaders getHttpHeaders() {
		var httpHeaders = new HttpHeaders();
		var token = System.getProperty("token");
		httpHeaders.set("Authorization", StringUtils.join("Bearer ", token));
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

	@Test
	@Order(3)
	void getTenantTest() {
		ResponseEntity<String> exchange = restTemplate.exchange(V1_USERS_URL_PATH + "tenants/101", HttpMethod.GET,
				new HttpEntity<>(getHttpHeaders()), String.class);
		assertThat(exchange).isNotNull();
		assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(4)
	@SneakyThrows
	void updateTenantTest() {
		var tenantsDTO = objectMapper.readValue(tenantJson(),TenantsDTO.class);
		tenantsDTO.setLeaseEndDate("24/03/2025");
		tenantsDTO.setLeaseStartDate("24/03/2022");
		HttpEntity<TenantsDTO> entity = new HttpEntity<>(tenantsDTO,getHttpHeaders());
		ResponseEntity<String> update = restTemplate.exchange(V1_USERS_URL_PATH + "tenants/101", HttpMethod.PUT, entity, String.class);
		assertThat(update).isNotNull();
		assertThat(update.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private String tenantJson() {
		return "{\n" +
				"  \"apartmentCost\": 94.81,\n" +
				"  \"type\": \"TENANT\",\n" +
				"  \"firstName\": \"test_e98e08f21a0c\",\n" +
				"  \"lastName\": \"test_fb5114647363\",\n" +
				"  \"phoneNo\": 9590319709,\n" +
				"  \"emailId\": \"testui@gmail.com\",\n" +
				"  \"addressLine\": \"test_8f795e58d76a\",\n" +
				"  \"city\": \"test_f26b0b390bdf\",\n" +
				"  \"state\": \"test_fb01039e8ada\",\n" +
				"  \"zipCode\": \"test_9dcd286330c0\",\n" +
				"  \"password\": \"test_ca770f93106f\"\n" +
				"}";
	}

	@Test
	@Order(5)
	void deleteTenantTest() {
		HttpEntity<TenantsDTO> entity = new HttpEntity<>(getHttpHeaders());
		ResponseEntity<String> update = restTemplate.exchange(V1_USERS_URL_PATH + "tenants/101", HttpMethod.DELETE, entity,
				String.class);
		assertThat(update).isNotNull();
		assertThat(update.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(6)
	@SneakyThrows
	void updateVendorTest() {
		var vendorDTO = objectMapper.readValue(vendorJson(),VendorDTO.class);
		HttpEntity<VendorDTO> entity = new HttpEntity<>(vendorDTO,getHttpHeaders());
		ResponseEntity<String> update = restTemplate.exchange(V1_USERS_URL_PATH + "vendor/101", HttpMethod.PUT, entity, String.class);
		assertThat(update).isNotNull();
		assertThat(update.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private String vendorJson() {
		return "{\n" +
				"  \"firstName\": \"Vendor_First_Name\",\n" +
				"  \"lastName\": \"JK_Vendor\",\n" +
				"  \"phoneNo\": \"9858121489\",\n" +
				"  \"emailId\": \"vendor.test@gmail.com\",\n" +
				"  \"addressLine\": \"PB\",\n" +
				"  \"city\": \"Solan\",\n" +
				"  \"state\": \"HP\",\n" +
				"  \"zipCode\": \"37883\",\n" +
				"  \"password\": \"vendor@123\",\n" +
				"  \"type\": \"VENDOR\"\n" +
				"}";
	}


	@Test
	@Order(7)
	void updateTenantInvalidRequestTest() {
		HttpEntity<TenantsDTO> entity = new HttpEntity<>(new TenantsDTO(),getHttpHeaders());
		ResponseEntity<String> update = restTemplate.exchange(V1_USERS_URL_PATH + "tenants/101", HttpMethod.PUT, entity,
				String.class);
		assertThat(update).isNotNull();
		assertThat(update.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
