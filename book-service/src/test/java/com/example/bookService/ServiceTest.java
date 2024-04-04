package com.example.bookService;

import com.example.bookService.DTO.BookDto;
import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Author.AuthorService;
import com.example.bookService.data.Book.Book;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Exceptions.AuthorIsNotException;
import com.example.bookService.data.Exceptions.AuthorNotFoundException;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockserver.model.HttpRequest.request;


@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, AuthorService.class, RestTemplateConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ServiceTest extends DataBaseSuite {
  @Autowired
  private BookService bookService;
  @Autowired
  private AuthorService authorService;
  @Container
  public static final MockServerContainer mockServer =
          new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("registry.service.base.url", mockServer::getEndpoint);
  }

  @BeforeAll
  static void setUp() {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
            .when(request()
                            .withMethod(String.valueOf(HttpMethod.POST))
                            .withHeader("X-REQUEST-ID")
                            .withPath("/api/registry"))
            .respond(new HttpResponse()
                    .withBody("{\"isValid\": \"true\"}")
                    .withHeader("Content-Type", "application/json"));
  }
  @Test
  void create() throws InvalidDataException, AuthorNotFoundException, AuthorIsNotException {
    Author author = authorService.createAuthor("Alexander", "Pushkin");
    Book book = bookService.createBook("Evgeniy Onegin", author.getAuthorId(), UUID.randomUUID().toString());
    assertEquals("Pushkin", author.getLastName());
    assertEquals("Alexander", author.getFirstName());
  }

  @Test
  void findById() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException, AuthorIsNotException {
    Author author = authorService.createAuthor("Lev", "Tolstoy");
    Book book = bookService.createBook("Война и Мир", author.getAuthorId(), UUID.randomUUID().toString());
    BookDto getBook = bookService.findBookById(book.getId());
    Author getAuthor = authorService.findAuthorById(author.getAuthorId());

    assertEquals(getBook.title(), book.getTitle());
    assertEquals(getBook.bookId(), book.getId());
    assertEquals(getAuthor.getAuthorId(), author.getAuthorId());
    assertEquals(getAuthor.getFirstName(), author.getFirstName());
    assertNotEquals(getAuthor.getLastName(), author.getFirstName());
  }

  @Test
  void update() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException, AuthorIsNotException {
    Author author = authorService.createAuthor("Nikita", "Tselischev");
    Book book = bookService.createBook("Homework", author.getAuthorId(), UUID.randomUUID().toString());

    bookService.updateBook(book.getId(), "Wow wow");
    BookDto updateBook = bookService.findBookById(book.getId());
    authorService.updateAuthor(author.getAuthorId(), "Bob","Job");
    Author updateAuthor = authorService.findAuthorById(author.getAuthorId());

    assertEquals(updateBook.title(), "Wow wow");
    assertEquals(updateAuthor.getFirstName(), "Bob");
    assertEquals(updateAuthor.getLastName(), "Job");
  }

  @Test
  void delete() throws InvalidDataException, BookNotFoundException, AuthorNotFoundException, AuthorIsNotException {
    Author author = authorService.createAuthor("Stephen", "King");
    Book book = bookService.createBook("Green Mile", author.getAuthorId(), UUID.randomUUID().toString());

    bookService.deleteBook(book.getId());
    authorService.deleteAuthor(author.getAuthorId());

    assertThrows(BookNotFoundException.class, () -> bookService.findBookById(book.getId()));
    assertThrows(AuthorNotFoundException.class, () -> authorService.findAuthorById(author.getAuthorId()));
  }
}
