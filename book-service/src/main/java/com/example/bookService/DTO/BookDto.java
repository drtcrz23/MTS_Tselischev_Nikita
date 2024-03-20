package com.example.bookService.DTO;

import com.example.bookService.data.Tag.Tag;

import java.util.Set;

public record BookDto(Long bookId, String title, Set<Tag> tags) {
}
