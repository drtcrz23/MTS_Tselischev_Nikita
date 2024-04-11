package com.example.bookpurchaseservice.DTO;

import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(@NotNull String name, @NotNull int balance) {
}
