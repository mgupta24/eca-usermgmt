package com.eca.usermgmt.service.notification;

import com.eca.usermgmt.service.notifiation.NotificationService;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class KafkaNotificationServiceTest {
	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@InjectMocks
	private NotificationService kafkaNotificationService;

	@Mock
	private ListenableFutureCallback<SendResult<String, String>> mockProducerCallback;

	@Test
	void kafkaNotificationTest() {
		ReflectionTestUtils.setField(kafkaNotificationService, "kafkaEnabled", true);
		ReflectionTestUtils.setField(kafkaNotificationService, "topicName", "test");
		ListenableFuture listenableFuture = mock(ListenableFuture.class);
		SendResult sendResult = mock(SendResult.class);
		RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("test", 0), 1L, 0, 0L, 0, 0);
		when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
		when(kafkaTemplate.send(eq("test"),anyString())).thenReturn(listenableFuture);
		kafkaNotificationService.sendNotification("test-message");
		verify(kafkaTemplate,times(1)).send(anyString(),eq("test-message"));
	}

	@Test
	void kafkaNotificationNoCallTest() {
		ReflectionTestUtils.setField(kafkaNotificationService, "kafkaEnabled", false);
		ReflectionTestUtils.setField(kafkaNotificationService, "topicName", "test");
		kafkaNotificationService.sendNotification("test-message");
		verify(kafkaTemplate,times(0)).send(anyString(),eq("test"));
	}
}
