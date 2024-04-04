package com.example.authorRegistryService.DTO;

import jakarta.validation.constraints.NotNull;

public record AuthorRegistryRequest (@NotNull String firstName,
                                     @NotNull String lastName,
                                     @NotNull String bookName) {
}