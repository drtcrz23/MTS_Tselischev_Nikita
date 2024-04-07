package com.example.bookratingservice;

import com.example.bookratingservice.DTO.BookRatingRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
  private final ObjectMapper objectMapper;
  private final Producer producer;

  public Consumer(ObjectMapper objectMapper, Producer producer) {
    this.objectMapper = objectMapper;
    this.producer = producer;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void processCookieMatching(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    BookRatingRequest parsedMessage = objectMapper.readValue(message, BookRatingRequest.class);
    LOGGER.info("Retrieved message {}", message);
    producer.stubRating(parsedMessage.bookId());
    acknowledgment.acknowledge();
  }
}