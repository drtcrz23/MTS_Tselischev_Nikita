package com.example.bookService.data.Book;

import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Tag.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "books")
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull(message = "")
  @Column(name = "title")
  private String title;

  @ManyToOne(fetch = EAGER)
  @JoinColumn(name = "author_id", insertable = false, updatable = false)
  private Author author;

  @Column(name = "author_id")
  private Long authorId;

  @ManyToMany(fetch = EAGER, cascade = PERSIST)
  @JoinTable(
          name = "books_tags",
          joinColumns = @JoinColumn(name = "book_id"),
          inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  protected Book() {
  }

  public Book(String title, Long authorId) {
    this.title = title;
    this.authorId = authorId;
  }

  public void addTag(Tag tag) {
    tags.add(tag);
  }

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public String getTitle() {
    return title;
  }
}
