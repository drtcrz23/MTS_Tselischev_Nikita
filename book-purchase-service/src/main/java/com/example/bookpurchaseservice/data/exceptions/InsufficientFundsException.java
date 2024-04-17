package com.example.bookpurchaseservice.data.exceptions;

public class InsufficientFundsException extends Exception{
  public InsufficientFundsException() {
    super ("insufficient funds");
  }
}
