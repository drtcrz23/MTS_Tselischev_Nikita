package com.example.bookService.DTO.request;

import jakarta.validation.constraints.NotNull;

public record TagRequestUpdate (@NotNull String name){
}
