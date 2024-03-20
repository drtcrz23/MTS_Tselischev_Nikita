package com.example.bookService.data.Exceptions;

public class BookNotFoundException extends Exception {
  public BookNotFoundException(Long bookId) {
    super("Book with id " + bookId + " not found");
  }
}

