package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.BuyBookRequest;
import com.example.bookpurchaseservice.DTO.BuyBookResponse;
import com.example.bookpurchaseservice.data.exceptions.InsufficientFundsException;
import com.example.bookpurchaseservice.data.exceptions.UserNotFoundException;
import com.example.bookpurchaseservice.data.user.UserService;
import com.example.bookpurchaseservice.scheduler.OutboxScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuyBookConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(BuyBookConsumer.class);
  @Autowired
  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final OutboxScheduler outboxScheduler;

  public BuyBookConsumer(UserService userService, ObjectMapper objectMapper, OutboxScheduler outboxScheduler) {
    this.userService = userService;
    this.objectMapper = objectMapper;
    this.outboxScheduler = outboxScheduler;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void consumeBookPurchaseResponse(String message, Acknowledgment acknowledgment)
          throws JsonProcessingException {
    BuyBookRequest parsedMessage = objectMapper.readValue(message, BuyBookRequest.class);
    boolean isSuccess = true;
    try {
      userService.payBook(parsedMessage.userId(), parsedMessage.amount());
    } catch (InsufficientFundsException e){
      isSuccess = false;
    } catch (UserNotFoundException e) {
      throw new RuntimeException(e);
    }
    outboxScheduler.scheduleMsg(objectMapper.writeValueAsString(new BuyBookResponse(parsedMessage.bookId(), isSuccess)));
    LOGGER.info("Retrieved message {}", message);
    acknowledgment.acknowledge();
  }
}
