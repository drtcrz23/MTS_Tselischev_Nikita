package com.example.bookService.data.Author;

import com.example.bookService.data.Exceptions.AuthorNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthorService {
  private final AuthorRepository authorRepository;

  @Autowired
  public AuthorService(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Author createAuthor(String firstName, String lastName) throws InvalidDataException {
    if (firstName == null || lastName == null) throw new InvalidDataException();
    Author author = new Author(firstName, lastName);
    return authorRepository.save(author);
  }
  @Transactional(propagation = Propagation.REQUIRED)
  public Author findAuthorById(Long authorId) throws AuthorNotFoundException {
    Author author = authorRepository.findById(authorId).orElse(null);
    if (author == null) throw new AuthorNotFoundException(authorId);
    return author;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void updateAuthor(Long authorId, String firstName, String lastName) throws AuthorNotFoundException {
    Author author = authorRepository.findById(authorId).orElse(null);
    if (author == null) throw new AuthorNotFoundException(authorId);
    author.setFirstName(firstName);
    author.setLastName(lastName);
    authorRepository.save(author);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteAuthor(Long authorId) throws AuthorNotFoundException{
    var author = authorRepository.findById(authorId).orElse(null);
    if (author == null) throw new AuthorNotFoundException(authorId);
    authorRepository.deleteById(authorId);
  }
}
