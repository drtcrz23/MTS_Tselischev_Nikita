package com.example.bookService;

import com.example.bookService.Exceptions.BookNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookService {
  private final BookRepository bookRepository;

  @Autowired
  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public Book createBook(String author, String title, Set<String> tags) {
    var book = bookRepository.createBook(author, title, new HashSet<>(tags));
    return new Book(book.getId(), book.getAuthor(), book.getTitle(), book.getTags());
  }

  public Book findBookById(Long bookId) throws BookNotFoundException {
    return bookRepository.findBookById(bookId);
  }

  public List<Book> findAllBook() {
    var books = bookRepository.findAllBooks();
    return books.stream().toList();
  }

  public List<Book> findBooksByTag(String tag) {
    var books = bookRepository.findBooksByTags(tag);
    return books.stream().toList();
  }

  public void updateBook(Long bookId, String author, String title, Set<String> tags)
          throws BookNotFoundException {
    bookRepository.updateBook(bookId, author, title, tags);
  }

  public void deleteBook(Long bookId) throws BookNotFoundException {
    bookRepository.deleteBook(bookId);
  }
}
