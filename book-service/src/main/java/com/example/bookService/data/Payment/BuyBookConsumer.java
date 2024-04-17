package com.example.bookService.data.Payment;

import com.example.bookService.DTO.response.BuyBookResponse;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BuyBookConsumer {
  private final ObjectMapper objectMapper;
  @Autowired
  private final BookService bookService;

  private static final Logger LOGGER = LoggerFactory.getLogger(BuyBookConsumer.class);

  public BuyBookConsumer(ObjectMapper objectMapper, BookService booksService) {
    this.objectMapper = objectMapper;
    this.bookService = booksService;
  }

  @KafkaListener(topics = {"${topic-to-consume-buy_book-message}"})
  public void consumeBuyBookResponse(String message)
          throws BookNotFoundException, JsonProcessingException {
    BuyBookResponse parsedMessage = objectMapper.readValue(message, BuyBookResponse.class);

    if (parsedMessage.isSuccess()) {
      LOGGER.info("Book {} succeed buy", parsedMessage.bookId());
      bookService.updatePaymentStatus(parsedMessage.bookId(), true);
    } else {
      LOGGER.info("Book {} failed buy", parsedMessage.bookId());
      bookService.updatePaymentStatus(parsedMessage.bookId(), false);
    }
  }
}
