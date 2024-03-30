package com.example.bookService;

import com.example.bookService.DTO.request.AuthorRegistryRequest;
import com.example.bookService.DTO.response.AuthorRegistryResponse;
import com.example.bookService.data.Author.AuthorService;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.data.Exceptions.CreateBookException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:test.properties")
@Import({BookService.class, AuthorService.class, RateLimiterAutoConfiguration.class})
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RateLimiterTest extends DataBaseSuite{
  @MockBean
  private RestTemplate restTemplate;
  @Autowired
  private BookService bookService;
  @Autowired
  private AuthorService authorService;

  @Test
  void shouldRejectRequestAfterFirstServerSlowResponse() throws InvalidDataException {
    var uuid = UUID.randomUUID().toString();
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-REQUEST-ID", uuid);
    var author = authorService.createAuthor("Лев", "Толстой");
    when(restTemplate.exchange(
            "/api/registry",
            HttpMethod.POST,
            new HttpEntity<>(
                    new AuthorRegistryRequest(
                            author.getFirstName(), author.getLastName(), "Война и мир"),
                    headers),
            AuthorRegistryResponse.class)).thenAnswer((Answer<ResponseEntity<AuthorRegistryResponse>>) invocation -> {
      Thread.sleep(2000);
      return new ResponseEntity<>(new AuthorRegistryResponse(true), HttpStatus.OK);
    });
    assertDoesNotThrow(
            () -> bookService.createBook("Война и мир", author.getAuthorId(), uuid)
    );

    assertThrows(
            CreateBookException.class,
            () -> bookService.createBook("Евгений Онегин", author.getAuthorId(), uuid)
    );
  }
}
