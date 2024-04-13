package com.example.bookService;

import com.example.bookService.DTO.response.BookRatingResponse;
import com.example.bookService.DTO.response.BuyBookResponse;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Payment.BuyBookConsumer;
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
import static org.mockito.Mockito.times;

@SpringBootTest(
        classes = {BuyBookConsumer.class, BookService.class},
        properties = {
                "topic-to-consume-buy_book-message=some-test-topic",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
@Import({KafkaAutoConfiguration.class, BuyBookConsumerTest.ObjectMapperTestConfig.class})
@Testcontainers
public class BuyBookConsumerTest extends DataBaseSuite {
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
  @MockBean
  private BookService bookService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private BuyBookConsumer buyBookConsumer;

  @Test
  void shouldSendMessageToPaySuccessfully() throws JsonProcessingException {
    kafkaTemplate.send("some-test-topic",
            objectMapper.writeValueAsString(new BuyBookResponse(56L, true)));

    await().atMost(Duration.ofSeconds(5))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(
                    bookService, times(1)).updatePaymentStatus(56L, true)
            );
  }
  @Test
  void shouldSendMessageToPayFailed() throws JsonProcessingException {
    kafkaTemplate.send("some-test-topic",
            objectMapper.writeValueAsString(new BuyBookResponse(56L, false)));

    await().atMost(Duration.ofSeconds(5))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(
                    bookService, times(1)).updatePaymentStatus(56L, false)
            );
  }
}
