package com.example.bookService;

import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Author.AuthorService;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Exceptions.CreateBookException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockserver.model.HttpRequest.request;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({BookService.class, AuthorService.class, RestTemplateConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TimeOutTest extends DataBaseSuite {
  @Autowired
  private BookService bookService;
  @Autowired
  private AuthorService authorService;
  @Container
  public static final MockServerContainer mockServer =
          new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));
  @Test
  void shouldCreateBook() throws InvalidDataException {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
            .when(request()
                    .withMethod(String.valueOf(HttpMethod.POST))
                    .withHeader("X-REQUEST-ID")
                    .withPath("/api/registry"))
            .respond(req -> {
              Thread.sleep(3000);
              return HttpResponse.response()
                      .withBody("{\"isValid\": \"true\"}")
                      .withHeader("Content-Type", "application/json");
            });
    Author author = authorService.createAuthor("Лев", "Толстой");
    assertThrows(
            CreateBookException.class,
            () -> bookService.createBook("Война и мир", author.getAuthorId(), UUID.randomUUID().toString()));
  }
}
