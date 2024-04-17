package com.example.bookService.data.Payment;

import com.example.bookService.DTO.request.BuyBookRequest;
import com.example.bookService.data.Book.Book;
import com.example.bookService.data.Book.BookRepository;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Outbox.OutboxScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  private final BookRepository bookRepository;
  private final ObjectMapper objectMapper;
  private final OutboxScheduler outboxScheduler;

  public PaymentService(BookRepository bookRepository, ObjectMapper objectMapper, OutboxScheduler outboxScheduler) {
    this.bookRepository = bookRepository;
    this.objectMapper = objectMapper;
    this.outboxScheduler = outboxScheduler;
  }

  public void buyBookById(Long bookId, Long userId) throws BookNotFoundException, JsonProcessingException {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) throw new BookNotFoundException(bookId);

    if (book.getPaymentStatus() == PaymentStatus.PAY_IN_PROCESS ||
            book.getPaymentStatus() == PaymentStatus.PAY_DONE) {
      return;
    }
    outboxScheduler.scheduleMsg(objectMapper.writeValueAsString(new BuyBookRequest(bookId, 100, userId)));
  }
}
