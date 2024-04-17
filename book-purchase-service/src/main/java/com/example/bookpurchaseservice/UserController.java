package com.example.bookpurchaseservice;

import com.example.bookpurchaseservice.DTO.UserCreateRequest;
import com.example.bookpurchaseservice.DTO.UserUpdateRequest;
import com.example.bookpurchaseservice.data.exceptions.InvalidDataException;
import com.example.bookpurchaseservice.data.exceptions.UserNotFoundException;
import com.example.bookpurchaseservice.data.user.User;
import com.example.bookpurchaseservice.data.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping()
  public User createUser(@NotNull @RequestBody @Valid UserCreateRequest body) throws InvalidDataException {
    return userService.createUser(body.name(), body.balance());
  }

  @GetMapping("{id}")
  public User findUserById(@NotNull @PathVariable("id") Long id) throws UserNotFoundException {
    return userService.findUserById(id);
  }

  @PutMapping("{id}")
  public void updateUser(@NotNull @PathVariable("id") Long userId,
                           @NotNull @RequestBody @Valid UserUpdateRequest body) throws UserNotFoundException {
    userService.updateUser(userId, body.name(), body.balance());
  }

  @DeleteMapping("{id}")
  public void deleteUser(@NotNull @PathVariable("id") Long userId) throws UserNotFoundException {
    userService.deleteUser(userId);
  }

  @ExceptionHandler
  public ResponseEntity<String> userNotFoundException(UserNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}

