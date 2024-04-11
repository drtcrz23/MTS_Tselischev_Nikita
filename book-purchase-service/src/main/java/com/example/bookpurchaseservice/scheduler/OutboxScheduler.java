package com.example.bookpurchaseservice.scheduler;

import com.example.bookpurchaseservice.BuyBookConsumer;
import com.example.bookpurchaseservice.data.outbox.OutboxRecord;
import com.example.bookpurchaseservice.data.outbox.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class OutboxScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxScheduler.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final OutboxRepository outboxRepository;

  public OutboxScheduler(KafkaTemplate<String, String> kafkaTemplate, @Value("${topic-to-send-message}") String topic, OutboxRepository outboxRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @Scheduled(fixedDelay = 10000)
  public void processOutbox() {
    List<OutboxRecord> result = outboxRepository.findAll();
    for (OutboxRecord outboxRecord : result) {
      CompletableFuture<SendResult<String, String>> sendResult =
              kafkaTemplate.send(topic, outboxRecord.getData());
      // block on sendResult until finished
    }
    outboxRepository.deleteAll(result);
  }

  @Transactional
  public void scheduleMsg(String data) {
    outboxRepository.save(new OutboxRecord(data));
  }
}
