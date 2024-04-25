package com.example.bookService.data.Outbox;

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
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final OutboxRepository outboxRepository;

  public OutboxScheduler(KafkaTemplate<String, String> kafkaTemplate, @Value("${topic-to-send-buy_book-message}") String topic, OutboxRepository outboxRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @Scheduled(fixedDelay = 1000000)
  public void processOutbox() {
    List<OutboxRecord> result = outboxRepository.findAll();
    for (OutboxRecord outboxRecord : result) {
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
