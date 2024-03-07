package com.example.bookService;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Book {
  private static final AtomicLong ATOMIC_LONG = new AtomicLong(0);
  private Long id;
  private String author;
  private String title;
  private Set<String> tags;

  public Book(Long id, @NotNull String author, @NotNull String title, @NotNull Set<String> tags) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.tags = tags;
  }
  public static Long generateId() {
    return ATOMIC_LONG.incrementAndGet();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return Objects.equals(id, book.id) && Objects.equals(author, book.author) && Objects.equals(title, book.title) && Objects.equals(tags, book.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, author, title, tags);
  }
}
