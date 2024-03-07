package com.example.bookService.Controller;

import com.example.bookService.Book;
import com.example.bookService.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BookViewController {
  private final BookService bookService;

  public BookViewController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping("/books")
  public String viewBooks(Model model) {
    List<Book> books = bookService.findAllBook();
    model.addAttribute("books", books);
    return "books";
  }
}
