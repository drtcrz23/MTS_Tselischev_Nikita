package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.BuyBookRequest;
import com.example.bookpurchaseservice.DTO.BuyBookResponse;
import com.example.bookpurchaseservice.data.exceptions.InvalidDataException;
import com.example.bookpurchaseservice.data.exceptions.UserNotFoundException;
import com.example.bookpurchaseservice.data.user.User;
import com.example.bookpurchaseservice.data.user.UserService;
import com.example.bookpurchaseservice.scheduler.OutboxScheduler;
import com.example.bookpurchaseservice.scheduler.SchedulerConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
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
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(
        properties = {
                "topic-to-send-message=some-test-topic",
                "topic-to-consume-message=test-request-topic",
                "spring.kafka.consumer.auto-offset-reset=earliest"
        })
@Import({
        BuyBookConsumer.class,
        KafkaAutoConfiguration.class,
        SchedulerConfig.class,
        OutboxScheduler.class
})
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceSuccessTest extends DataBaseSuite {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
          new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private UserService userService;
  @Autowired
  private BuyBookConsumer buyBookConsumer;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Test
  void shouldSuccessTest() throws JsonProcessingException, InterruptedException, UserNotFoundException, InvalidDataException {
    User user = userService.createUser("Nikita", 1000);

    kafkaTemplate.send(
            "test-request-topic", objectMapper.writeValueAsString(new BuyBookRequest(1L, 100, user.getId())));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("some-test-topic"));
    Thread.sleep(10000);

    ConsumerRecords<String, String> records = consumer.poll();
//    assertEquals(1, records.count());

    records.iterator().forEachRemaining(
            record -> {
              BuyBookResponse message;
              try {
                message = objectMapper.readValue(record.value(), BuyBookResponse.class);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              assertTrue(message.isSuccess());
              assertEquals(1L, message.bookId());
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
