package com.example.bookService.Controller;

import com.example.bookService.Book;
import com.example.bookService.BookService;
import com.example.bookService.Controller.DTO.BookRequestCreate;
import com.example.bookService.Controller.DTO.BookRequestUpdate;
import com.example.bookService.Exceptions.BookNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping()
  public Book create(@NotNull @RequestBody @Valid BookRequestCreate body) {
    return bookService.createBook(body.title(), body.author(), body.tags());
  }

//  @GetMapping("/books")
//  public List<Book> findAllBooks() {
//    return bookService.findAllBook();
//  }

  @GetMapping("/books/{id}")
  public Book findBookById(@NotNull @PathVariable("id") Long id) throws BookNotFoundException {
    return bookService.findBookById(id);
  }
  @GetMapping("/books")
  public List<Book> findBookByTag(@RequestParam(required = false) String tag) {
    return bookService.findBooksByTag(tag);
  }

  @PutMapping("/{id}")
  public void updateBook(@NotNull @PathVariable Long id,
                         @NotNull @RequestBody @Valid BookRequestUpdate body)
          throws BookNotFoundException {
    bookService.updateBook(id, body.title(), body.author(), body.tags());
  }

  @DeleteMapping("/{id}")
  public void deleteBook(@NotNull @PathVariable Long id) throws BookNotFoundException {
    bookService.deleteBook(id);
  }

  @ExceptionHandler
  public ResponseEntity<String> bookNotFoundException(BookNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}
