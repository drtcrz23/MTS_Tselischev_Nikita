package com.example.bookService.data.Exceptions;

public class TagNotFoundException extends Exception {
  public TagNotFoundException(Long tagId) {
    super("Tag with id " + tagId + " not found");
  }
}
