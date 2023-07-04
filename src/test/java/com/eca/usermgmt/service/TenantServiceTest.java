package com.eca.usermgmt.service;

import com.eca.usermgmt.cache.CacheService;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.entity.Tenant;
import com.eca.usermgmt.entity.User;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.repository.TenantsRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.service.impl.TenantServiceImpl;
import com.eca.usermgmt.service.notifiation.NotificationService;
import com.eca.usermgmt.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TenantServiceTest {

	@InjectMocks
	private TenantServiceImpl tenantService;
	@Mock
	private Validator validator;
	@Mock
	private TenantsRepository tenantsRepository;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private ObjectMapper objectMapper;

	@InjectMocks
	private ModelMapper modelMapper;

	@InjectMocks
	private JsonUtils jsonUtils;

	@Mock
	private KafkaTemplate<String,String> kafkaTemplate;

	@Mock
	private NotificationService kafkaNotificationService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private CacheService cacheService;

	@BeforeEach
	public void init() {
		objectMapper.registerModules(new JavaTimeModule());
		ReflectionTestUtils.setField(tenantService,"kafkaEnabled",false);
		when(passwordEncoder.encode(any())).thenReturn("JIwowu");
		TenantsDTO tenantsDTO = new TenantsDTO();
		tenantsDTO.setId(101L);
		tenantsDTO.setApartmentCost(BigDecimal.valueOf(3838333));
		when(cacheService.getFromCache(any())).thenReturn(tenantsDTO);
	}


	@Test
	@SneakyThrows
	void updateTenantTest() {
		ReflectionTestUtils.setField(tenantService,"modelMapper",modelMapper);
		var entity = objectMapper.readValue(tenantJson(), Tenant.class);
		when(tenantsRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(entity));
		var request = modelMapper.map(entity, TenantsDTO.class);
		entity.setFirstName("TEST_Z1");
		entity.setLastName("Z2_LASTNAME");
		when(tenantsRepository.saveAndFlush(Mockito.any())).thenReturn(entity);
		var userDetails = new User();
		userDetails.setUsername("singh@gmailcom");
		userDetails.setPassword("Test@12333");
		userDetails.setId(1L);
		userDetails.setTenant(new Tenant());
		when(userRepository.getUserDetailsByTenantId(anyLong())).thenReturn(userDetails);
		when(userRepository.save(any())).thenReturn(userDetails);
		ReflectionTestUtils.setField(tenantService,"kafkaEnabled",true);
		ReflectionTestUtils.setField(tenantService,"jsonUtils",jsonUtils);
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		ReflectionTestUtils.setField(kafkaNotificationService,"kafkaEnabled",false);
		ReflectionTestUtils.setField(tenantService,"cacheExpiry","30");
		doNothing().when(cacheService).addToCache(eq("TENANT_1"),any(),eq(30), eq(TimeUnit.MINUTES));
		ResponseEntity<BaseResponse> baseResponseResponseEntity = tenantService.updateTenant(1L, request);
		assertThat(baseResponseResponseEntity)
				.isNotNull();
		assertThat(baseResponseResponseEntity.getStatusCode())
				.isEqualTo(HttpStatus.ACCEPTED);
		verify(kafkaNotificationService,times(1)).sendNotification(anyString());
	}

	private String tenantJson() {
		return "{\n" +
				"  \"id\": 1,\n" +
				"  \"apartmentCost\": 94.81,\n" +
				"  \"type\": \"TENANT\",\n" +
				"  \"firstName\": \"test_e98e08f21a0c\",\n" +
				"  \"lastName\": \"test_fb5114647363\",\n" +
				"  \"phoneNo\": 1,\n" +
				"  \"emailId\": \"testui@gmail.com\",\n" +
				"  \"addressLine\": \"test_8f795e58d76a\",\n" +
				"  \"city\": \"test_f26b0b390bdf\",\n" +
				"  \"state\": \"test_fb01039e8ada\",\n" +
				"  \"zipCode\": \"test_9dcd286330c0\",\n" +
				"  \"password\": \"test_ca770f93106f\"\n" +
				"}";
	}

	@Test
	void testEnum() {
		assertThat(TypeOfUser.getUserType("vendor")).isEqualTo(TypeOfUser.VENDOR);
	}

	@Test
	@SneakyThrows
	void testDelete() {
		var userDetails = deleteTenantRequest();
		when(userRepository.getUserDetailsByTenantId(anyLong())).thenReturn(userDetails);
		doNothing().when(userRepository).delete(any());
		doNothing().when(tenantsRepository).delete(any());
		tenantService.deleteTenant(1L);
		verify(userRepository,times(1)).getUserDetailsByTenantId(anyLong());
	}

	private User deleteTenantRequest() {
		var tenants = new Tenant();
		tenants.setEmailId("test@gmail.com");
		when(tenantsRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(tenants));
		var userDetails = new User();
		userDetails.setTenant(tenants);
		return userDetails;
	}

	@Test
	void invalidJsonObjectTest() {
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		assertThatThrownBy(() -> jsonUtils.jsonToObject("{", TenantsDTO.class))
				.isInstanceOf(UserManagementException.class);
	}
	@Test
	void invalidToJsonTest() {
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		assertThat(jsonUtils.toJson("\"{\"JAI~----\"}")).isNotNull();
		assertThatExceptionOfType(UserManagementException.class)
				.isThrownBy(() -> jsonUtils.toMap("\"{\"JAI\"}"));
	}

	@Test
	void kafkaDeleteTest() {
		ReflectionTestUtils.setField(jsonUtils,"objectMapper",objectMapper);
		ReflectionTestUtils.setField(tenantService,"kafkaEnabled",true);
		ReflectionTestUtils.setField(tenantService,"modelMapper",new ModelMapper());
		ReflectionTestUtils.setField(tenantService,"jsonUtils",jsonUtils);
		ReflectionTestUtils.setField(kafkaNotificationService,"kafkaEnabled",false);
		ReflectionTestUtils.setField(kafkaNotificationService,"topicName","test");
		var userDetails = deleteTenantRequest();
		userDetails.setUserPhoneNumber(3883883L);
		userDetails.setUsername("test@gmail.com");
		userDetails.setPassword("test@123");
		when(userRepository.getUserDetailsByTenantId(anyLong())).thenReturn(userDetails);
		doNothing().when(userRepository).delete(any());
		doNothing().when(tenantsRepository).delete(any());
		doNothing().when(kafkaNotificationService).sendNotification(anyString());
		tenantService.deleteTenant(1L);
		verify(kafkaNotificationService,times(1)).sendNotification(anyString());
	}
}
