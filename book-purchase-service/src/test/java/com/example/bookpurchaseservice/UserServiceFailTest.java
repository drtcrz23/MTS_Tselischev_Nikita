package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.BuyBookRequest;
import com.example.bookpurchaseservice.data.exceptions.InvalidDataException;
import com.example.bookpurchaseservice.data.user.UserService;
import com.example.bookpurchaseservice.scheduler.OutboxScheduler;
import com.example.bookpurchaseservice.scheduler.SchedulerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(
        properties = {
                "topic-to-send-message=test-response-topic",
                "topic-to-consume-message=test-request-topic",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
@Import({
        BuyBookConsumer.class,
        KafkaAutoConfiguration.class,
        SchedulerConfig.class,
        OutboxScheduler.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserServiceFailTest extends DataBaseSuite {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
          new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private UserService userService;
  @Autowired private BuyBookConsumer buyBookConsumer;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  void shouldFailTest() throws JsonProcessingException {
    try {
      userService.createUser("Nikita", 10);
    } catch (InvalidDataException e) {
      throw new RuntimeException(e);
    }

    kafkaTemplate.send(
            "test-request-topic", objectMapper.writeValueAsString(new BuyBookRequest(1L, 100, 1L)));
    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
            record -> {
              BuyBookRequest message;
              try {
                message = objectMapper.readValue(record.value(), BuyBookRequest.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              assertEquals(new BuyBookRequest(1L, 100, 1L), message);
            }
    );
  }

  private static class KafkaTestConsumer {
    private final KafkaConsumer<String, String> consumer;

    public KafkaTestConsumer(String bootstrapServers, String groupId) {
      Properties props = new Properties();

      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      this.consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics) {
      consumer.subscribe(topics);
    }

    public ConsumerRecords<String, String> poll() {
      return consumer.poll(Duration.ofSeconds(5));
    }
  }
}
