package com.eca.usermgmt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnExpression("${app.cache.enabled:false}")
public class RedisConfiguration {
	@Value("${app.redis.host:localhost}")
	private String redisHostName;

	@Value("${app.redis.port:6379}")
	private int redisPort;

	@Value("${app.redis.password}")
	private String password;

	@Value("${app.redis.connectTimeOutInSeconds:60}")
	private long connectTimeout;


	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHostName);
		redisStandaloneConfiguration.setPort(redisPort);
		if(StringUtils.isNotBlank(password)) {
			redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		}
		var jedisClientConfiguration = JedisClientConfiguration.builder();
		jedisClientConfiguration.connectTimeout(Duration.ofSeconds(connectTimeout));
		return new JedisConnectionFactory(redisStandaloneConfiguration,
				jedisClientConfiguration.build());
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
		final RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
		return template;
	}
}
