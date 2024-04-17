package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.UserCreateRequest;
import com.example.bookpurchaseservice.DTO.UserUpdateRequest;
import com.example.bookpurchaseservice.data.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest extends DataBaseSuite{
  @Autowired
  private TestRestTemplate rest;

  @Test
  void endToEndTest() {

    var userCreateRequest = new UserCreateRequest("Nikita", 1000);
    ResponseEntity<User> createUserResponse =
            rest.postForEntity("/api/users", userCreateRequest, User.class);
    assertTrue(createUserResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createUserResponse.getStatusCode());
    User createUserResponseBody = createUserResponse.getBody();
    long userId = createUserResponseBody.getId();

    ResponseEntity<User> getUserResponse =
            rest.getForEntity("/api/users/{id}", User.class, Map.of("id", createUserResponseBody.getId()));
    assertTrue(getUserResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getUserResponse.getStatusCode());

    User getUserResponseBody = getUserResponse.getBody();
    assertEquals("Nikita", getUserResponseBody.getName());
    assertEquals(1000, getUserResponseBody.getBalance());

    UserUpdateRequest userRequestUpdate = new UserUpdateRequest("WOW", 100);
    rest.put("/api/users/{id}", userRequestUpdate,  Map.of("id", userId));
    ResponseEntity<User> getUserResponseUpdate =
            rest.getForEntity("/api/users/{id}", User.class, Map.of("id", userId));
    User getUserResponseUpdateBody = getUserResponseUpdate.getBody();
    assertNotEquals(getUserResponseUpdateBody.getName(), "Nikita");
    assertEquals(getUserResponseUpdateBody.getBalance(), 100);

    rest.delete("/api/users/{id}", Map.of("id", userId));
    ResponseEntity<Void> getUserResponseDelete =
            rest.getForEntity("/api/users/{id}", Void.class, Map.of("id", userId));
    Void getUserResponseDeleteBody = getUserResponseDelete.getBody();
    assertEquals(HttpStatus.NOT_FOUND, getUserResponseDelete.getStatusCode());
  }
}
