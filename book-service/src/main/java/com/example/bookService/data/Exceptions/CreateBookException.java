package com.example.bookService.data.Exceptions;

public class CreateBookException extends RuntimeException{
  public CreateBookException(String message, Throwable cause) {
    super(message, cause);
  }
}
