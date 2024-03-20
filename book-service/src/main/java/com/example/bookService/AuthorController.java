package com.example.bookService;


import com.example.bookService.DTO.request.AuthorRequestCreate;
import com.example.bookService.DTO.request.AuthorRequestUpdate;
import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Author.AuthorService;
import com.example.bookService.data.Exceptions.AuthorNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@Validated
public class AuthorController {
  private final AuthorService authorService;

  @Autowired
  public AuthorController(AuthorService authorService) {
    this.authorService = authorService;
  }

  @PostMapping()
  public Author createAuthor(@NotNull @RequestBody @Valid AuthorRequestCreate body) throws InvalidDataException {
    return authorService.createAuthor(body.firstName(), body.lastName());
  }

  @GetMapping("{id}")
  public Author findAuthorById(@NotNull @PathVariable("id") Long id) throws AuthorNotFoundException {
    return authorService.findAuthorById(id);
  }

  @PutMapping("{id}")
  public void updateAuthor(@NotNull @PathVariable("id") Long authorId,
                           @NotNull @RequestBody @Valid AuthorRequestUpdate body) throws AuthorNotFoundException {
    authorService.updateAuthor(authorId, body.newFirstName(), body.newLastName());
  }

  @DeleteMapping("{id}")
  public void deleteAuthor(@NotNull @PathVariable("id") Long authorId) throws AuthorNotFoundException {
    authorService.deleteAuthor(authorId);
  }

  @ExceptionHandler
  public ResponseEntity<String> authorNotFoundException(AuthorNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}
