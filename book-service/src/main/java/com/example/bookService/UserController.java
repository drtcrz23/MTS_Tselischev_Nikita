package com.example.bookService;

import com.example.bookService.DTO.UserRegisterRequest;
import com.example.bookService.data.User.Users;
import com.example.bookService.data.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/user")
@Validated
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping
  @Transactional
  public Users register(@RequestBody UserRegisterRequest request) {
    return userRepository.save(new Users(
            request.username(), passwordEncoder.encode(request.password()), request.roles()));
  }
}
