package com.example.bookService.Controller;

import com.example.bookService.Book;
import com.example.bookService.Controller.DTO.BookRequestCreate;
import com.example.bookService.Controller.DTO.BookRequestUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookControllerTest {
  @Autowired
  private TestRestTemplate rest;

  @Test
  void endToEndTest() {
    var bookRequest = new BookRequestCreate("Michael", "Brand new book", Set.of("Comedy", "Mystery"));
    ResponseEntity<Book> createBookResponse =
            rest.postForEntity("/api", bookRequest, Book.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    Book createBookResponseBody = createBookResponse.getBody();
    long id = createBookResponseBody.getId();

    ResponseEntity<Book> getBookResponse =
            rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.getId()));
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());

    Book getBookResponseBody = getBookResponse.getBody();
    assertEquals("Michael", getBookResponseBody.getAuthor());
    assertEquals("Brand new book", getBookResponseBody.getTitle());

    BookRequestUpdate bookRequestUpdate = new BookRequestUpdate("Nikita", "Tselischev", Set.of("MIPT"));
    rest.put("/api/books/{id}", bookRequestUpdate,  Map.of("id", id));
    ResponseEntity<Book> getBookResponseUpdate =
            rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", id));
    Book getBookResponseUpdateBody = getBookResponseUpdate.getBody();
    assertEquals(getBookResponseUpdateBody.getAuthor(), "Nikita");
    assertEquals(getBookResponseUpdateBody.getTitle(), "Tselischev");
    assertEquals(getBookResponseUpdateBody.getTags(), Set.of("MIPT"));

    rest.delete("/api/books/{id}", Map.of("id", id));
    ResponseEntity<Void> getBookResponseDelete =
            rest.getForEntity("/api/books/{id}", Void.class, Map.of("id", id));
    Void getBookResponseDeleteBody = getBookResponseDelete.getBody();
    assertEquals(HttpStatus.NOT_FOUND, getBookResponseDelete.getStatusCode());
  }
}
