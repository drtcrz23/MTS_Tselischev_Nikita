package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.BuyBookRequest;
import com.example.bookpurchaseservice.data.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;

public class BookBuyConsumerTest {
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ObjectMapper objectMapper;
  @MockBean
  private UserService userService;
  @Autowired private BuyBookConsumer buyBookConsumer;

  @Test
  void shouldReceiveSuccessMessageFromKafkaSuccessfully() throws JsonProcessingException {
    kafkaTemplate.send(
            "some-test-topic", objectMapper.writeValueAsString(new BuyBookRequest(1L, 100, 1L)));

    await()
            .atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(userService, times(1)).payBook(1L, 100));
  }
}
