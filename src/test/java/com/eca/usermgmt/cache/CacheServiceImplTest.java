package com.eca.usermgmt.cache;

import com.eca.usermgmt.dto.OwnerDTO;
import com.eca.usermgmt.dto.TenantsDTO;
import com.eca.usermgmt.dto.UserCommonInfoDTO;
import com.eca.usermgmt.dto.VendorDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
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
@Slf4j
class CacheServiceImplTest {

	public static final int APPLICATION_CACHE_EXPIRY_IN_SECONDS = (180 * 1000);
	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String,Object> valueOperations;

	@Mock
	private RedisOperations<String,Object> redisOperations;

	@InjectMocks
	private CacheServiceImpl cacheService;

	Map<String, CacheContainer> userTypeCache = new ConcurrentHashMap<>();

	@BeforeEach
	void init() {
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(valueOperations.getOperations()).thenReturn(redisOperations);
	}

	@Test
	void removeCacheTest() {
		when(redisOperations.delete(anyString())).thenReturn(true);
		cacheService.remove("OWNER_1");
		verify(redisTemplate,times(1)).opsForValue();
	}

	@Test
	void addToCacheTest() {
		var tenantsDTO = getTenant();
		var key = "TEENAT_KEY_101";
		doNothing().when(valueOperations).set(eq(key),any(TenantsDTO.class),anyLong(),any());
		cacheService.addToCache(key, tenantsDTO,10, TimeUnit.MINUTES);
		verify(redisTemplate,times(1)).opsForValue();
		when(valueOperations.get(key)).thenReturn(tenantsDTO);
		Object cacheObject = cacheService.getFromCache(key);
		assertThat(cacheObject).isNotNull();
	}


	@Test
	void cacheContainerTest() {
		var cacheContainer = new CacheContainer(new VendorDTO(), System.currentTimeMillis());
		assertThat(CacheContainer.isCacheNotExpired(cacheContainer, APPLICATION_CACHE_EXPIRY_IN_SECONDS)).isTrue();
	}

	@Test
	void cacheContainer() {
		userTypeCache.put("OWNER",new CacheContainer(new OwnerDTO(),System.currentTimeMillis()));
		userTypeCache.put("TENANT",new CacheContainer(new TenantsDTO(),System.currentTimeMillis()));
		Assertions.assertThat(getUserType("OWNER")).isNotNull();
		Assertions.assertThat(getUserType("TENANT")).isNotNull();
		Assertions.assertThat(getUserType("VENDOR")).isNotNull();
		Assertions.assertThat(getUserType("VENDOR")).isNotNull();

	}

	public UserCommonInfoDTO getUserType(String type) {
		final CacheContainer cacheContainer = userTypeCache.get(type);
		UserCommonInfoDTO userCommonInfoDTO = null;
		if (CacheContainer.isCacheNotExpired(cacheContainer, APPLICATION_CACHE_EXPIRY_IN_SECONDS)) {
			log.info("### Cache UserDTO ");
			userCommonInfoDTO = (UserCommonInfoDTO) cacheContainer.getCacheObject();
		} else {
			log.info("### Calling External Not Cache ");
			userCommonInfoDTO = getForExternalSource(type);
			userTypeCache.put(type, new CacheContainer(userCommonInfoDTO, System.currentTimeMillis()));
		}
		return userCommonInfoDTO;
	}

	private UserCommonInfoDTO getForExternalSource(String type) {
		return type !=null && type.equalsIgnoreCase("OWNER") ? new OwnerDTO()
				: type !=null && type.equalsIgnoreCase("TENANT") ? new TenantsDTO() : new VendorDTO();
	}

	private static TenantsDTO getTenant() {
		var tenantsDTO = new TenantsDTO();
		tenantsDTO.setId(0L);
		tenantsDTO.setApartmentCost(new BigDecimal("0"));
		tenantsDTO.setLeaseStartDate("2022-02-02");
		tenantsDTO.setLeaseEndDate("2024-02-02");
		tenantsDTO.setFirstName("Ram");
		tenantsDTO.setLastName("Singh");
		tenantsDTO.setPhoneNo(10292992922L);
		tenantsDTO.setEmailId("ramsingh@gmail.com");
		tenantsDTO.setAddressLine("Marheen");
		tenantsDTO.setCity("Kathua");
		tenantsDTO.setState("JK");
		tenantsDTO.setZipCode("181402");
		tenantsDTO.setPassword("testpwd");
		return tenantsDTO;
	}

}
