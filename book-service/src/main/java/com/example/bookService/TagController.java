package com.example.bookService;

import com.example.bookService.DTO.request.TagRequestCreate;
import com.example.bookService.DTO.request.TagRequestUpdate;
import com.example.bookService.data.Exceptions.InvalidDataException;
import com.example.bookService.data.Exceptions.TagNotFoundException;
import com.example.bookService.data.Tag.Tag;
import com.example.bookService.data.Tag.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/tags")
public class TagController {

  private final TagService tagService;

  @Autowired
  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @PostMapping()
  public Tag createTag(@NotNull @RequestBody @Valid TagRequestCreate body) throws InvalidDataException {
    return tagService.createTag(body.name());
  }

  @GetMapping("{id}")
  public Tag findTagById(@PathVariable("id") Long tagId) throws TagNotFoundException {
    return tagService.findTagById(tagId);
  }

  @PutMapping("{id}")
  public void updateTag(@NotNull @PathVariable("id") Long tagId,
                        @NotNull @RequestBody @Valid TagRequestUpdate body) throws TagNotFoundException {
    tagService.updateTag(tagId, body.name());
  }

  @DeleteMapping("{id}")
  public void deleteTag(@NotNull @PathVariable("id") Long tagId) throws TagNotFoundException {
    tagService.deleteTag(tagId);
  }

  @ExceptionHandler
  public ResponseEntity<String> tagNotFoundException(TagNotFoundException exception) {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}
