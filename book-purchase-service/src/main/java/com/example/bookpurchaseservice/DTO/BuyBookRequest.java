package com.example.bookpurchaseservice.DTO;

public record BuyBookRequest(Long bookId, int amount, Long userId) {
}
