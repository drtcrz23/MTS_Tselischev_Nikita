package com.example.bookratingservice;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RatingService {
  private final Random random = new Random();

  public int getRating(Long bookId) {
    return random.nextInt(0, 100);
  }
}
