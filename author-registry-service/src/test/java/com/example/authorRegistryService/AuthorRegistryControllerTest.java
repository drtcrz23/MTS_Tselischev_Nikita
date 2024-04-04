package com.example.authorRegistryService;

import com.example.authorRegistryService.DTO.AuthorRegistryRequest;
import com.example.authorRegistryService.DTO.AuthorRegistryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthorRegistryControllerTest {
  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void checkIsNotValid() {
    HttpHeaders headers = new HttpHeaders();

    headers.add("X-REQUEST-ID", UUID.randomUUID().toString());

    ResponseEntity<AuthorRegistryResponse> authorRegistry =
            restTemplate.exchange(
                    "/api/registry",
                    HttpMethod.POST,
                    new HttpEntity<>(
                            new AuthorRegistryRequest("Nikita", "Tselischev", "apple"),
                            headers),
                    AuthorRegistryResponse.class);
    assertEquals(authorRegistry.getBody().isValid(), false);
  }
@Test
  void checkIsValid() {
    HttpHeaders headers = new HttpHeaders();

    headers.add("X-REQUEST-ID", UUID.randomUUID().toString());

    ResponseEntity<AuthorRegistryResponse> authorRegistry =
            restTemplate.exchange(
                    "/api/registry",
                    HttpMethod.POST,
                    new HttpEntity<>(
                            new AuthorRegistryRequest("Лев", "Толстой", "Анна Каренина"),
                            headers),
                    AuthorRegistryResponse.class);
    assertEquals(authorRegistry.getBody().isValid(), true);
  }
}
