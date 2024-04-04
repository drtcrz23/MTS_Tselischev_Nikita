package com.example.bookService.data.Book;

import com.example.bookService.DTO.BookDto;
import com.example.bookService.DTO.request.AuthorRegistryRequest;
import com.example.bookService.DTO.response.AuthorRegistryResponse;
import com.example.bookService.data.Author.AuthorRepository;
import com.example.bookService.data.Exceptions.*;
import com.example.bookService.data.Tag.Tag;
import com.example.bookService.data.Tag.TagRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Service
public class BookService {
  private final BookRepository bookRepository;
  private final TagRepository tagRepository;
  private final AuthorRepository authorRepository;
  public final RestTemplate restTemplate;

  @Autowired
  public BookService(BookRepository bookRepository, TagRepository tagRepository, AuthorRepository authorRepository, RestTemplate restTemplate) {
    this.bookRepository = bookRepository;
    this.tagRepository = tagRepository;
    this.authorRepository = authorRepository;
    this.restTemplate = restTemplate;
  }

  @RateLimiter(name = "createBook", fallbackMethod = "fallbackRateLimiter")
  @CircuitBreaker(name = "createBook", fallbackMethod = "fallbackCircuitBreaker")
  @Transactional()
  public Book createBook(String title, Long authorId, String requestId) throws InvalidDataException, AuthorNotFoundException, AuthorIsNotException {
    try {
      if (title == null || authorId == null) throw new InvalidDataException();
      var author = authorRepository.findById(authorId).orElse(null);
      if (author == null) throw new AuthorNotFoundException(authorId);

      HttpHeaders headers = new HttpHeaders();

      headers.add("X-REQUEST-ID", requestId);

      ResponseEntity<AuthorRegistryResponse> authorRegistry =
              restTemplate.exchange(
                      "/api/registry",
                      HttpMethod.POST,
                      new HttpEntity<>(
                              new AuthorRegistryRequest(author.getFirstName(), author.getLastName(), title),
                              headers),
                      AuthorRegistryResponse.class);

      if (!Objects.requireNonNull(authorRegistry.getBody()).isValid()) {
        throw new AuthorIsNotException(authorId);
      }
      return bookRepository.save(new Book(title, authorId));
    } catch (AuthorIsNotException e) {
      throw new AuthorIsNotException(authorId);
    } catch (AuthorNotFoundException e) {
      throw new AuthorNotFoundException(authorId);
    } catch (RestClientException e) {
      throw new CreateBookException("Error during book creation: " + e.getMessage(), e);
    }
  }

  public BookDto findBookById(Long bookId) throws BookNotFoundException {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) throw new BookNotFoundException(bookId);
    return new BookDto(book.getId(), book.getTitle(), book.getTags());
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void updateBook(Long bookId, String title) throws BookNotFoundException {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) throw new BookNotFoundException(bookId);
    book.setTitle(title);
    bookRepository.save(book);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteBook(Long bookId) throws BookNotFoundException {
    Book book = bookRepository.findById(bookId).orElse(null);
    if (book == null) throw new BookNotFoundException(bookId);
    bookRepository.deleteById(bookId);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void addNewTag(Long bookId, Long tagId) throws TagNotFoundException, BookNotFoundException {
    Book book = bookRepository.findById(bookId).orElse(null);
    Tag tag = tagRepository.findById(tagId).orElse(null);
    if (tag == null) throw new TagNotFoundException(tagId);
    if (book == null) throw new BookNotFoundException(bookId);

    book.addTag(tag);
    bookRepository.save(book);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void removeTag(Long bookId, Long tagId) throws TagNotFoundException, BookNotFoundException {
    Book book = bookRepository.findById(bookId).orElse(null);
    Tag tag = tagRepository.findById(tagId).orElse(null);
    if (tag == null) throw new TagNotFoundException(tagId);
    if (book == null) throw new BookNotFoundException(bookId);
    book.getTags().remove(tag);
    bookRepository.save(book);
  }

  public Book fallbackRateLimiter(String title, Long authorId, String requestId, Throwable e) {
    throw new CreateBookException(e.getMessage(), e);
  }
  public Book fallbackCircuitBreaker(String title, Long authorId, String requestId, Throwable e) {
    throw new CreateBookException(e.getMessage(), e);
  }
}