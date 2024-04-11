package com.example.bookpurchaseservice.DTO;

import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(@NotNull String name, @NotNull int balance) {
}
