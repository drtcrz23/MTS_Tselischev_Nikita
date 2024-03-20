package com.example.bookService;

import com.example.bookService.DTO.BookDto;
import com.example.bookService.DTO.request.BookRequestCreate;
import com.example.bookService.data.Book.Book;
import com.example.bookService.data.Book.BookService;
import com.example.bookService.DTO.request.BookRequestUpdate;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import com.example.bookService.data.Exceptions.TagNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
  private final BookService bookService;

  @Autowired
  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping()
  public Book create(@NotNull @RequestBody @Valid BookRequestCreate body) throws InvalidDataException {
    return bookService.createBook(body.title(), body.id());
  }

//  @GetMapping("/books")
//  public List<Book> findAllBooks() {
//    return bookService.findAllBook();
//  }

  @GetMapping("{id}")
  public BookDto findBookById(@NotNull @PathVariable("id") Long id) throws BookNotFoundException {
    return bookService.findBookById(id);
  }

  @PutMapping("{id}")
  public void updateBook(@NotNull @PathVariable("id") Long id,
                         @NotNull @RequestBody @Valid BookRequestUpdate body)
          throws BookNotFoundException {
    bookService.updateBook(id, body.title());
  }

  @DeleteMapping("{id}")
  public void deleteBook(@NotNull @PathVariable("id") Long id) throws BookNotFoundException {
    bookService.deleteBook(id);
  }

  @PostMapping("/{bookId}/tags/{tagId}")
  public void addTagToBook(@NotNull @PathVariable Long bookId,
                           @NotNull @PathVariable Long tagId)
          throws TagNotFoundException, BookNotFoundException {
    bookService.addNewTag(bookId, tagId);
  }

  @DeleteMapping("/{bookId}/tags/{tagId}")
  public void deleteTagToBook(@NotNull @PathVariable Long bookId,
                              @NotNull @PathVariable Long tagId)
          throws TagNotFoundException, BookNotFoundException {
    bookService.removeTag(bookId, tagId);
  }

  @ExceptionHandler
  public ResponseEntity<String> bookNotFoundException(BookNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}
