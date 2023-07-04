package com.eca.usermgmt.service.notifiation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@ConditionalOnExpression("${app.kafka.enabled}")
public class NotificationService {

	@Autowired(required = false)
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${app.kafka.enabled}")
	private boolean kafkaEnabled;

	@Value("${app.topic.name}")
	private String topicName;

	public void sendNotification(String message) {
		 if(kafkaEnabled) {
			log.info("KafkaNotificationService topicName: {}  message :  {}  ",topicName,message);
			 kafkaTemplate.send(topicName,message);
		 }
	}
}
