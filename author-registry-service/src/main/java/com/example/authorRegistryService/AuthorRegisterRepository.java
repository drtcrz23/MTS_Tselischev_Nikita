package com.example.authorRegistryService;

import com.example.authorRegistryService.DTO.AuthorInfo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
@Repository
public class AuthorRegisterRepository {
  private final Map<String, AuthorInfo> bookAuthor;

  public AuthorRegisterRepository() {
    this.bookAuthor = new HashMap<>();
    bookAuthor.put("1000000", new AuthorInfo("lol", "kek"));
  }
  public boolean isValid(AuthorInfo authorInfo, String bookName) {
    var author = this.bookAuthor.getOrDefault(bookName, null);
    if (author == null) return false;
    return author.firstName().equals(authorInfo.firstName())
            && author.lastName().equals(authorInfo.lastName());
  }
}
