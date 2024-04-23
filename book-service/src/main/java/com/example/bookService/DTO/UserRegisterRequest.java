package com.example.bookService.DTO;

import com.example.bookService.data.User.Users;

import java.util.Set;

public record UserRegisterRequest(String username, String password, Set<Users.Role> roles) {
}
