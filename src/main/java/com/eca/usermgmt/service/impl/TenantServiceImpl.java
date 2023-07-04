package com.eca.usermgmt.service.impl;

import com.eca.usermgmt.cache.CacheService;
import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.dto.KafkaMessageDTO;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.response.BaseResponse;
import com.eca.usermgmt.dto.response.TenantsResponse;
import com.eca.usermgmt.entity.Tenant;
import com.eca.usermgmt.enums.TypeOfUser;
import com.eca.usermgmt.exception.TenantNotFoundException;
import com.eca.usermgmt.exception.UserManagementException;
import com.eca.usermgmt.repository.TenantsRepository;
import com.eca.usermgmt.repository.UserRepository;
import com.eca.usermgmt.service.TenantService;
import com.eca.usermgmt.service.notifiation.NotificationService;
import com.eca.usermgmt.utils.DateUtils;
import com.eca.usermgmt.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.eca.usermgmt.dto.KafkaMessageDTO.EventType.CREATE;
import static com.eca.usermgmt.dto.KafkaMessageDTO.EventType.DELETE;
import static com.eca.usermgmt.dto.KafkaMessageDTO.EventType.UPDATE;

@Service
@Slf4j
public class TenantServiceImpl implements TenantService {
	@Autowired
	private TenantsRepository tenantsRepository;
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Validator validator;

	@Autowired(required = false)
	private NotificationService notificationService;

	@Autowired
	private JsonUtils jsonUtils;

	@Value("${app.kafka.enabled}")
	private boolean kafkaEnabled;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired(required = false)
	private CacheService cacheService;

	@Value("${app.cache.expiryTimeoutInMinutes:30}")
	private String cacheExpiry;

	@Value("${app.cache.enabled}")
	private boolean redisCacheEnable;

	@Override
	public ResponseEntity<BaseResponse> getAllTenants() {
		var findAllTenants = tenantsRepository.findAll();
		log.info("TenantServiceImpl:: getAllTenants {} ",findAllTenants);
		var collect = Optional.of(findAllTenants)
				.orElse(Collections.emptyList())
				.stream()
				.map(this::toTenantsDto)
				.collect(Collectors.toList());
		return toBaseResponse(collect, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<BaseResponse> getTenantById(Long id) {
		var cacheResponse = getFromCache(id);
		log.info("TenantServiceImpl::getTenantById Cache Data {} ",cacheResponse);
		if (cacheResponse == null) {
			return Optional.of( tenantsRepository.findById(id))
					.filter(Optional::isPresent)
					.map(tenants -> {
						var tenantDto = modelMapper.map(tenants, TenantsDTO.class);
						addToCache(id, tenantDto);
						return toBaseResponse(List.of(tenantDto), HttpStatus.OK);
					})
					.orElseThrow(() -> new TenantNotFoundException(id));
		}
		return toBaseResponse(List.of(cacheResponse),HttpStatus.OK);
	}

	private void addToCache(Long id, TenantsDTO tenantDto) {
		if(redisCacheEnable) {
			cacheService.addToCache(tenantKey(id), tenantDto, Long.parseLong(cacheExpiry),
					TimeUnit.MINUTES);
		}
	}

	private TenantsDTO getFromCache(Long id) {
		if(redisCacheEnable) {
			Object fromCache = cacheService.getFromCache(tenantKey(id));
			log.info("TenantServiceImpl:: getFromCache {} ",fromCache);
			if(fromCache instanceof Map) {
				return jsonUtils.convertObject(fromCache,TenantsDTO.class);
			}
			return (TenantsDTO) fromCache;
		}
		return null;
	}

	private static String tenantKey(Long id) {
		return StringUtils.join(UserConstants.TYPE_TENANT, "_", id);
	}

	@Override
	public ResponseEntity<BaseResponse> createTenant(TenantsDTO requestDto) {
		var objectToSave = dtoToEntity(requestDto);
		log.info("TenantServiceImpl::createTenant request {}",requestDto);
		Set<ConstraintViolation<Tenant>> validate = validator.validate(objectToSave);
		if (!validate.isEmpty()) {
			log.error("TenantServiceImpl::createTenant ConstraintViolationException {}",validate);
			throw new ConstraintViolationException(validate);
		}
		return Optional.of(tenantsRepository.save(objectToSave))
				.map(entity -> {
					var tenantsDto = toTenantsDto(entity);
					pushNotification(tenantsDto, CREATE);
					return toBaseResponse(List.of(tenantsDto), HttpStatus.CREATED);
				})
				.orElseThrow(() -> new UserManagementException(UserConstants.TENANT_CREATION_FAILED));
	}

	private <T> void pushNotification(T toKafka, KafkaMessageDTO.EventType eventType) {
		if (kafkaEnabled) {
			var kafkaMessageDTO = modelMapper.map(toKafka, KafkaMessageDTO.class);
			kafkaMessageDTO.setEventType(eventType);
			log.info("TenantServiceImpl::pushToKafka {} ",kafkaMessageDTO);
			if(StringUtils.equalsAnyIgnoreCase(eventType.toString(), CREATE.toString(),UPDATE.toString())) {
				kafkaMessageDTO.getTenant().setId(null);
			}
			notificationService.sendNotification(jsonUtils.toJson(kafkaMessageDTO));
		}
	}


	private Tenant dtoToEntity(TenantsDTO requestDto) {
		var objectToSave = modelMapper.map(requestDto, Tenant.class);
		objectToSave.setLeaseEndDate(DateUtils.stringToLocalDateTime(requestDto.getLeaseEndDate()));
		objectToSave.setLeaseStartDate(DateUtils.stringToLocalDateTime(requestDto.getLeaseStartDate()));
		objectToSave.setType(TypeOfUser.TENANT.toString());
		return objectToSave;
	}

	private TenantsDTO toTenantsDto(Tenant entity) {
		return modelMapper.map(entity, TenantsDTO.class);
	}

	@Override
	public ResponseEntity<BaseResponse> updateTenant(Long id, TenantsDTO tenantsDTO) {
		var tenant = getTenant(id);
		log.info("TenantServiceImpl::updateTenant getTenant {}",tenant);
		prepareEntity(tenantsDTO, tenant);
		Set<ConstraintViolation<Tenant>> validate = validator.validate(tenant);
		if (!validate.isEmpty()) {
			log.error("TenantServiceImpl::updateTenant ConstraintViolationException {} ",validate);
			throw new ConstraintViolationException(validate);
		}
		log.info("TenantServiceImpl Saving Tenant {} ", tenant);
		return Optional.of(tenantsRepository.saveAndFlush(tenant))
				.map(savedTenant -> {
					Optional.ofNullable(userRepository.getUserDetailsByTenantId(savedTenant.getId()))
							.ifPresent(userDetails -> {
								userDetails.setUserPhoneNumber(tenant.getPhoneNo());
								userDetails.setPassword(passwordEncoder.encode(tenantsDTO.getPassword()));
								userRepository.save(userDetails);
								pushNotification(userDetails, UPDATE);
							});
					var tenantDto = modelMapper.map(tenant, TenantsDTO.class);
					removeFromCache(id);
					addToCache(id, tenantDto);
					return toBaseResponse(List.of(tenantDto), HttpStatus.ACCEPTED);
				})
				.orElseThrow(() -> new UserManagementException(UserConstants.TENANT_UPDATION_FAILED));
	}

	private void removeFromCache(Long id) {
		if(redisCacheEnable) {
			cacheService.remove(tenantKey(id));
		}
	}


	private Tenant getTenant(Long id) {
		return tenantsRepository.findById(id)
				.orElseThrow(() -> new TenantNotFoundException(id));
	}

	private void prepareEntity(TenantsDTO request, Tenant tenant) {
		var tenantsDto = modelMapper.map(request, TenantsDTO.class);
		tenant.setApartmentCost(tenantsDto.getApartmentCost());
		if (tenantsDto.getLeaseStartDate() != null) {
			tenant.setLeaseStartDate(DateUtils.stringToLocalDateTime(tenantsDto.getLeaseStartDate()));
		}
		if (tenantsDto.getLeaseEndDate() != null) {
			tenant.setLeaseEndDate(DateUtils.stringToLocalDateTime(tenantsDto.getLeaseEndDate()));
		}
		if (StringUtils.isNotBlank(tenantsDto.getFirstName())) {
			tenant.setFirstName(tenantsDto.getFirstName());
		}
		if (StringUtils.isNotBlank(tenantsDto.getLastName())) {
			tenant.setLastName(tenantsDto.getLastName());
		}
		if (tenantsDto.getPhoneNo() !=null) {
			tenant.setPhoneNo(tenantsDto.getPhoneNo());
		}
		if (StringUtils.isNotBlank(tenantsDto.getAddressLine())) {
			tenant.setAddressLine(tenantsDto.getAddressLine());
		}
		if (StringUtils.isNotBlank(tenantsDto.getCity())) {
			tenant.setCity(tenantsDto.getCity());
		}
		if (StringUtils.isNotBlank(tenantsDto.getState())) {
			tenant.setState(tenantsDto.getState());
		}
		if (StringUtils.isNotBlank(tenantsDto.getZipCode())) {
			tenant.setZipCode(tenantsDto.getZipCode());
		}
		tenant.setType(TypeOfUser.TENANT.toString());
	}

	@Override
	public void deleteTenant(Long id) {
		var user = tenantsRepository.findById(id)
				.map(key -> userRepository.getUserDetailsByTenantId(key.getId()))
				.orElseThrow(() -> new TenantNotFoundException(id));
		user.setTenant(null);
		log.info("TenantServiceImpl::deleteTenant {} user ", user);
		userRepository.delete(user);
		tenantsRepository.deleteById(id);
		pushNotification(user, DELETE);
	}

	private ResponseEntity<BaseResponse> toBaseResponse(List<TenantsDTO> tenants, HttpStatus httpStatus) {
		var buildResponse = new TenantsResponse();
		buildResponse.setTimestamp(LocalDateTime.now());
		buildResponse.setTenantsData(tenants);
		return new ResponseEntity<>(buildResponse, httpStatus);
	}
}
