package com.example.bookService.data.Exceptions;

public class AuthorIsNotException extends Exception {
  public AuthorIsNotException(Long authorId) {
    super("Author with id " + authorId + " not exist");
  }
}
