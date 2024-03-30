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
    bookAuthor.put("Война и мир", new AuthorInfo("Лев", "Толстой"));
    bookAuthor.put("Преступление и наказание", new AuthorInfo("Фёдор", "Достоевский"));
    bookAuthor.put("Мастер и Маргарита", new AuthorInfo("Михаил", "Булгаков"));
    bookAuthor.put("1984", new AuthorInfo("Джордж", "Оруэлл"));
    bookAuthor.put("Улисс", new AuthorInfo("Джеймс", "Джойс"));
    bookAuthor.put("Грозовой перевал", new AuthorInfo("Эмили", "Бронте"));
    bookAuthor.put("Маленький принц", new AuthorInfo("Антуан", "д'Сент-Экзюпери"));
    bookAuthor.put("Три товарища", new AuthorInfo("Эрих", "Мария Ремарк"));
    bookAuthor.put("Анна Каренина", new AuthorInfo("Лев", "Толстой"));
    bookAuthor.put("Евгений Онегин", new AuthorInfo("Александр", "Пушкин"));
  }
  public boolean isValid(AuthorInfo authorInfo, String bookName) {
    var author = this.bookAuthor.getOrDefault(bookName, null);
    if (author == null) return false;
    return author.firstName().equals(authorInfo.firstName())
            && author.lastName().equals(authorInfo.lastName());
  }
}
