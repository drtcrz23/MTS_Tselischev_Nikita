package com.example.bookService.data.Book;

import com.example.bookService.DTO.BookDto;
import com.example.bookService.data.Author.AuthorRepository;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import com.example.bookService.data.Exceptions.TagNotFoundException;
import com.example.bookService.data.Tag.Tag;
import com.example.bookService.data.Tag.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
  private final BookRepository bookRepository;
  private final TagRepository tagRepository;
  private final AuthorRepository authorRepository;

  @Autowired
  public BookService(BookRepository bookRepository, TagRepository tagRepository, AuthorRepository authorRepository) {
    this.bookRepository = bookRepository;
    this.tagRepository = tagRepository;
    this.authorRepository = authorRepository;
  }

  public Book createBook(String title, Long authorId) throws InvalidDataException {
    if (title == null || authorId == null) throw new InvalidDataException();
    var author = authorRepository.findById(authorId);
    if (author.isEmpty()) throw new InvalidDataException();
    return bookRepository.save(new Book(title, authorId));
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
}