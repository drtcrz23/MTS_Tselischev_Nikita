package com.example.bookService.Controller.DTO;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record BookRequestUpdate(@NotNull String author, @NotNull String title, @NotNull Set<String> tags) {
}
