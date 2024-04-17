package com.example.bookpurchaseservice.data.exceptions;

public class UserNotFoundException extends Exception{
  public UserNotFoundException(Long userId) {
    super("User with id " + userId + " not found");
  }

}
