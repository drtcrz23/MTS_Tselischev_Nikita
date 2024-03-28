package com.example.bookService;

import com.example.bookService.DTO.BookDto;
import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Author.AuthorService;
import com.example.bookService.data.Book.Book;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Exceptions.AuthorNotFoundException;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, AuthorService.class})
public class ServiceTest extends DataBaseSuite {
  @Autowired
  private BookService bookService;
  @Autowired
  private AuthorService authorService;

  @Test
  void create() throws InvalidDataException {
    Author author = authorService.createAuthor("Alexander", "Pushkin");
    Book book = bookService.createBook("Evgeniy Onegin", author.getAuthorId());
    assertEquals("Pushkin", author.getLastName());
    assertEquals("Alexander", author.getFirstName());
  }

  @Test
  void findById() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException {
    Author author = authorService.createAuthor("Lev", "Tolstoy");
    Book book = bookService.createBook("Война и Мир", author.getAuthorId());
    BookDto getBook = bookService.findBookById(book.getId());
    Author getAuthor = authorService.findAuthorById(author.getAuthorId());

    assertEquals(getBook.title(), book.getTitle());
    assertEquals(getBook.bookId(), book.getId());
    assertEquals(getAuthor.getAuthorId(), author.getAuthorId());
    assertEquals(getAuthor.getFirstName(), author.getFirstName());
    assertNotEquals(getAuthor.getLastName(), author.getFirstName());
  }

  @Test
  void update() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException {
    Author author = authorService.createAuthor("Nikita", "Tselischev");
    Book book = bookService.createBook("Homework", author.getAuthorId());

    bookService.updateBook(book.getId(), "Wow wow");
    BookDto updateBook = bookService.findBookById(book.getId());
    authorService.updateAuthor(author.getAuthorId(), "Bob","Job");
    Author updateAuthor = authorService.findAuthorById(author.getAuthorId());

    assertEquals(updateBook.title(), "Wow wow");
    assertEquals(updateAuthor.getFirstName(), "Bob");
    assertEquals(updateAuthor.getLastName(), "Job");
  }

  @Test
  void delete() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException {
    Author author = authorService.createAuthor("Stephen", "King");
    Book book = bookService.createBook("Green Mile", author.getAuthorId());

    bookService.deleteBook(book.getId());
    authorService.deleteAuthor(author.getAuthorId());

    assertThrows(BookNotFoundException.class, () -> bookService.findBookById(book.getId()));
    assertThrows(AuthorNotFoundException.class, () -> authorService.findAuthorById(author.getAuthorId()));
  }
}
