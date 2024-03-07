package com.example.bookService;

import com.example.bookService.Exceptions.BookNotFoundException;

import java.util.List;
import java.util.Set;

public interface BookRepository {
  Book createBook(String author, String title, Set<String> tags);
  void deleteBook(Long bookId) throws BookNotFoundException;
  void updateBook(Long bookId, String author, String title, Set<String> tags) throws BookNotFoundException;
  Book findBookById(Long bookId) throws BookNotFoundException;
  List<Book> findAllBooks();
  List<Book> findBooksByTags(String tags);
}
