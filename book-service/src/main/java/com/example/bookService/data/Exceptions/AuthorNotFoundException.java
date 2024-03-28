package com.example.bookService.data.Exceptions;

public class AuthorNotFoundException extends Exception {
  public AuthorNotFoundException(Long authorId) {
    super("Author with id " + authorId + " not found");
  }
}
