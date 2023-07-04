package com.eca.usermgmt.config;

import com.eca.usermgmt.service.notifiation.NotificationService;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnExpression("${app.kafka.enabled:false}")
public class KafkaConfiguration {
	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String bootstrapAddress;

	@Value("${app.topic.name}")
	private String topicName;

	@Value("${app.topic.partitions}")
	private Integer numPartitions;

	@Value("${app.topic.replica-factor}")
	private Short replicationFactor;

	@Bean
	public ProducerFactory<String,String> producerFactory() {
		Map<String,Object> configProp = new HashMap<>();
		configProp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		configProp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProp);
	}

	@Bean
	public KafkaTemplate<String,String> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public KafkaAdmin kafkaAdmin() {
		return new KafkaAdmin(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress));
	}

	@Bean
	public NewTopic newTopic() {
		return new NewTopic(topicName,numPartitions,replicationFactor);
	}

	@Bean
	public NotificationService kafkaNotificationService() {
		return new NotificationService();
	}
}

