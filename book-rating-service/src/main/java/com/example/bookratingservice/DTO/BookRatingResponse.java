package com.example.bookratingservice.DTO;

import io.swagger.v3.oas.models.security.SecurityScheme;

public record BookRatingResponse(Long bookId, Integer rating) {
}
