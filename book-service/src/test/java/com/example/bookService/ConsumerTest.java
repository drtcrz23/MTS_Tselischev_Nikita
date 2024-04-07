package com.example.bookService;

import com.example.bookService.DTO.response.BookRatingResponse;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Book.Consumer;
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
        classes = {Consumer.class},
        properties = {
                "topic-to-consume-message=some-test-topic",
                "spring.kafka.consumer.group-id=some-consumer-group",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        }
)
@Import({KafkaAutoConfiguration.class, ConsumerTest.ObjectMapperTestConfig.class})
@Testcontainers
public class ConsumerTest {
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @MockBean
  private BookService bookService;
  @Autowired
  private Consumer consumer;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws JsonProcessingException {
    kafkaTemplate.send("some-test-topic",
            objectMapper.writeValueAsString(new BookRatingResponse(56L, 50)));

    await().atMost(Duration.ofSeconds(5))
            .pollDelay(Duration.ofSeconds(1))
            .untilAsserted(() -> Mockito.verify(
                    bookService, times(1)).updateRating(56L, 50)
            );
  }
}
