package com.example.bookService.Exceptions;

public class BookNotFoundException extends Exception {
  public BookNotFoundException(Long bookId) {
    super("Book with id " + bookId + " not found");
  }
}

