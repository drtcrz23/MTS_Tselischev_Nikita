package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.BuyBookRequest;
import com.example.bookpurchaseservice.data.outbox.OutboxRepository;
import com.example.bookpurchaseservice.data.user.UserService;
import com.example.bookpurchaseservice.scheduler.OutboxScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
@SpringBootTest(
        classes = {BuyBookConsumer.class, UserService.class, OutboxScheduler.class},
        properties = {
                "topic-to-consume-message=some-test-topic",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
@Import({KafkaAutoConfiguration.class, BookBuyConsumerTest.ObjectMapperTestConfig.class})
@Testcontainers
public class BookBuyConsumerTest extends DataBaseSuite{
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
          new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired private ObjectMapper objectMapper;
  @MockBean
  private UserService userService;

  @MockBean
  private OutboxRepository outboxRepository;

  @Autowired private BuyBookConsumer buyBookConsumer;

  @Test
  void shouldReceiveSuccessMessageFromKafkaSuccessfully() throws JsonProcessingException {
    assertDoesNotThrow(() ->
            userService.createUser("Nikita", 1000));

    kafkaTemplate.send(
            "some-test-topic", objectMapper.writeValueAsString(new BuyBookRequest(5L, 100, 1L)));

    await()
            .atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(
                    userService, times(1)).payBook(1L, 100));
  }
}
