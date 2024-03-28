package com.example.bookService.DTO.request;

import jakarta.validation.constraints.NotNull;

public record AuthorRequestCreate(@NotNull String firstName, @NotNull String lastName) {
}
