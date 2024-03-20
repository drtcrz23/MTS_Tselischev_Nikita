package com.example.bookService.DTO.request;

import jakarta.validation.constraints.NotNull;

public record AuthorRequestUpdate(@NotNull String newFirstName, @NotNull String newLastName) {
}
