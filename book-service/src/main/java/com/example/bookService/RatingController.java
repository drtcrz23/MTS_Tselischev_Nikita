package com.example.bookService;

import com.example.bookService.data.Book.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rating")
@Validated
public class RatingController {
  private final Producer producer;
  @Autowired
  public RatingController(Producer producer) {
    this.producer = producer;
  }

  @PostMapping("{id}")
  public void stubRating(@NotNull @PathVariable("id") Long id) throws JsonProcessingException {
    producer.stubRating(id);
  }
}

