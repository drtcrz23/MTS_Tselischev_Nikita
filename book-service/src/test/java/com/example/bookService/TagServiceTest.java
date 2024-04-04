package com.example.bookService;

import com.example.bookService.data.Author.Author;
import com.example.bookService.data.Book.Book;
import com.example.bookService.data.Exceptions.AuthorNotFoundException;
import com.example.bookService.data.Exceptions.BookNotFoundException;
import com.example.bookService.data.Exceptions.InvalidDataException;
import com.example.bookService.data.Exceptions.TagNotFoundException;
import com.example.bookService.data.Tag.Tag;
import com.example.bookService.data.Tag.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TagService.class})
public class TagServiceTest extends DataBaseSuite {
  @Autowired
  private TagService tagService;

  @Test
  void createTag() throws InvalidDataException {
    Tag tag = tagService.createTag("novel");

    assertEquals("novel", tag.getName());
    assertNotEquals("123", tag.getName());
  }

  @Test
  void findTagById() throws InvalidDataException, TagNotFoundException {
    Tag tag = tagService.createTag("novel");

    Tag newTag = tagService.findTagById(tag.getId());

    assertEquals(tag.getId(), newTag.getId());
    assertEquals(tag.getName(), newTag.getName());
  }

  @Test
  void updateTag() throws InvalidDataException, TagNotFoundException {
    Tag tag = tagService.createTag("novel");

    tagService.updateTag(tag.getId(), "non-fiction");

    Tag newTag = tagService.findTagById(tag.getId());
    assertEquals("non-fiction", newTag.getName());
  }

  @Test
  void deleteTag() throws InvalidDataException, TagNotFoundException {
    Tag tag = tagService.createTag("novel");
    tagService.deleteTag(tag.getId());
    assertThrows(TagNotFoundException.class, () -> tagService.findTagById(tag.getId()));
  }
}
