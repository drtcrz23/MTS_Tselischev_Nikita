package com.example.bookService.data.Exceptions;

public class InvalidDataException extends Exception{
  public InvalidDataException() {
    super("Некорректные данные");
  }
}
