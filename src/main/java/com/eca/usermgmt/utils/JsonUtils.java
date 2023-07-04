package com.eca.usermgmt.utils;

import com.eca.usermgmt.constants.UserConstants;
import com.eca.usermgmt.exception.UserManagementException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class JsonUtils {
	@Autowired
	private ObjectMapper objectMapper;

	public <T> String toJson(T t) {
		try {
			return objectMapper.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			log.error(StringUtils.join(UserConstants.JSON_PROCESSING_ERROR,"{}"),e.getMessage());
			throw new UserManagementException(UserConstants.JSON_PROCESSING_ERROR, e);
		}
	}

	public Map<String, String> toMap(String json) {
		try {
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			log.error(StringUtils.join(UserConstants.JSON_PROCESSING_ERROR,"{}"),e.getMessage());
			throw new UserManagementException(UserConstants.JSON_PROCESSING_ERROR, e);
		}
	}

	public <T> T jsonToObject(String json, Class<T> aClass) {
		try {
			return objectMapper.readValue(json, aClass);
		} catch (JsonProcessingException e) {
			log.error(StringUtils.join(UserConstants.JSON_PROCESSING_ERROR,"{}"),e.getMessage());
			throw new UserManagementException(UserConstants.JSON_PROCESSING_ERROR, e);
		}
	}

	public <T, E> T convertObject(E e, Class<T> aClass) {
		return objectMapper.convertValue(e, aClass);
	}

}
