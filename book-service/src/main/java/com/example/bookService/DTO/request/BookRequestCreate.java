package com.example.bookService.DTO.request;

import jakarta.validation.constraints.NotNull;

public record BookRequestCreate(@NotNull String title, @NotNull Long id) {
}
