package com.example.bookpurchaseservice.data.exceptions;

public class InvalidDataException extends Exception {
  public InvalidDataException() {
    super("Некорректные данные");
  }
}
