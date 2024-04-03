package com.example.bookService.data.Book;

import com.example.bookService.DTO.response.BookRatingResponse;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

  private final ObjectMapper objectMapper;
  @Autowired
  private final BookService bookService;

  public Consumer(ObjectMapper objectMapper, BookService bookService) {
    this.objectMapper = objectMapper;
    this.bookService = bookService;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void processCookieMatching(String message) throws JsonProcessingException, BookNotFoundException {
    BookRatingResponse parsedMessage = objectMapper.readValue(message, BookRatingResponse.class);
    LOGGER.info("Retrieved message {}", message);
    bookService.updateRating(parsedMessage.bookId(), parsedMessage.rating());
  }
}
