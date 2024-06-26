package com.example.bookService;

import com.example.bookService.DTO.request.AuthorRequestCreate;
import com.example.bookService.data.Author.*;
import com.example.bookService.DTO.request.AuthorRequestUpdate;
import com.example.bookService.data.User.UserRepository;
import com.example.bookService.data.User.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorControllerTest extends DataBaseSuite {
  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    userRepository.deleteAll();
    Set<Users.Role> set = new HashSet<>();
    set.add(Users.Role.valueOf("ADMIN"));
    userRepository.save(new Users("admin", encoder.encode("123"), set));
    rest = rest.withBasicAuth("admin", "123");
  }

  @Test
  void endToEndTest() {

    var authorRequest = new AuthorRequestCreate("Nikita", "Tselischev");
    ResponseEntity<Author> createAuthorResponse =
            rest.postForEntity("/api/authors", authorRequest, Author.class);
    assertTrue(createAuthorResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createAuthorResponse.getStatusCode());
    Author createAuthorResponseBody = createAuthorResponse.getBody();
    long authorId = createAuthorResponseBody.getAuthorId();

    ResponseEntity<Author> getAuthorResponse =
            rest.getForEntity("/api/authors/{id}", Author.class, Map.of("id", createAuthorResponseBody.getAuthorId()));
    assertTrue(getAuthorResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getAuthorResponse.getStatusCode());

    Author getAuthorResponseBody = getAuthorResponse.getBody();
    assertEquals("Nikita", getAuthorResponseBody.getFirstName());
    assertEquals("Tselischev", getAuthorResponseBody.getLastName());

    AuthorRequestUpdate AuthorRequestUpdate = new AuthorRequestUpdate("WOW", "RABBIT");
    rest.put("/api/authors/{id}", AuthorRequestUpdate, Map.of("id", authorId));
    ResponseEntity<Author> getAuthorResponseUpdate =
            rest.getForEntity("/api/authors/{id}", Author.class, Map.of("id", authorId));
    Author getAuthorResponseUpdateBody = getAuthorResponseUpdate.getBody();
    assertNotEquals(getAuthorResponseUpdateBody.getFirstName(), "Nikita");
    assertEquals(getAuthorResponseUpdateBody.getLastName(), "RABBIT");

    rest.delete("/api/Authors/{id}", Map.of("id", authorId));
    ResponseEntity<Void> getAuthorResponseDelete =
            rest.getForEntity("/api/Authors/{id}", Void.class, Map.of("id", authorId));
    Void getAuthorResponseDeleteBody = getAuthorResponseDelete.getBody();
    assertEquals(HttpStatus.NOT_FOUND, getAuthorResponseDelete.getStatusCode());
  }
}