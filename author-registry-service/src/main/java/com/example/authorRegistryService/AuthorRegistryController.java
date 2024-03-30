package com.example.authorRegistryService;

import com.example.authorRegistryService.DTO.AuthorInfo;
import com.example.authorRegistryService.DTO.AuthorRegistryRequest;
import com.example.authorRegistryService.DTO.AuthorRegistryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@Validated
public class AuthorRegistryController {
  private final AuthorRegisterRepository authorRegisterRepository;
  private final Map<String, AuthorInfo> authors = new ConcurrentHashMap<>();

  @Autowired
  public AuthorRegistryController(AuthorRegisterRepository authorRegisterRepository) {
    this.authorRegisterRepository = authorRegisterRepository;
  }

  @PostMapping("/registry")
  public AuthorRegistryResponse getAuthorRegistry(@NotNull @RequestHeader("X-REQUEST-ID") String requestId,
                                                  @NotNull @RequestBody @Valid AuthorRegistryRequest body) {
    AuthorInfo authorInfo = authors.computeIfAbsent(
            requestId,
            k -> {
              return new AuthorInfo(body.firstName(), body.lastName());
            }
    );
    return new AuthorRegistryResponse(authorRegisterRepository.isValid(authorInfo, body.bookName()));
  }
}
