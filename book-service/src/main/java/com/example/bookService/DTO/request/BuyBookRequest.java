package com.example.bookService.DTO.request;

public record BuyBookRequest(Long bookId, int amount, Long userId) {}