package com.example.bookService;

import com.example.bookService.Exceptions.BookNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class InMemoryBookRepository implements BookRepository {
  private final AtomicLong ATOMIC_LONG;
  private final Map<Long, Book> booksMap;

  public InMemoryBookRepository() {
    this.ATOMIC_LONG = new AtomicLong(0);
    this.booksMap = new ConcurrentHashMap<>();
  }

  @Override
  public Book createBook(String author, String title, Set<String> tags) {
    Book book = new Book(ATOMIC_LONG.getAndIncrement(), author, title, tags);
    booksMap.put(book.getId(), book);
    return book;
  }

  @Override
  public void deleteBook(Long bookId) throws BookNotFoundException {
    booksMap.remove(bookId);
  }

  @Override
  public void updateBook(Long bookId, String author, String title, Set<String> tags) throws BookNotFoundException {
    Book book = findBookById(bookId);
    if (book == null) {
      throw new BookNotFoundException(bookId);
    }
    booksMap.put(book.getId(), new Book(book.getId(), author, title, tags));
  }

  @Override
  public Book findBookById(Long bookId) throws BookNotFoundException{
    if (booksMap.containsKey(bookId)) {
      return booksMap.get(bookId);
    } else {
      throw new BookNotFoundException(bookId);
    }
  }

  @Override
  public List<Book> findAllBooks() {
    return booksMap.values().stream().toList();
  }

  @Override
  public List<Book> findBooksByTags(String tag) {
    List<Book> booksByTag = new ArrayList<>();
    for (Book book : booksMap.values()) {
      if (book.getTags().contains(tag)) {
        booksByTag.add(book);
      }
    }
    return booksByTag;
  }
}
