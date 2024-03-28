package com.example.bookService.data.Tag;

import com.example.bookService.data.Exceptions.InvalidDataException;
import com.example.bookService.data.Exceptions.TagNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {
  private final TagRepository tagRepository;

  @Autowired
  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  public Tag createTag(String name) throws InvalidDataException {
    if (name == null) throw new InvalidDataException();
    Tag tag = new Tag(name);
    return tagRepository.save(tag);
  }

  public Tag findTagById(Long tagId) throws TagNotFoundException {
    Tag tag = tagRepository.findById(tagId).orElse(null);
    if (tag == null) throw new TagNotFoundException(tagId);
    return tag;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void updateTag(Long tagId, String newName) throws TagNotFoundException {
    Tag tag = tagRepository.findById(tagId).orElse(null);
    if (tag == null) throw new TagNotFoundException(tagId);
    tag.setName(newName);
    tagRepository.save(tag);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteTag(Long tagId) throws TagNotFoundException {
    var tag = tagRepository.findById(tagId).orElse(null);
    if (tag == null) throw new TagNotFoundException(tagId);
    tagRepository.deleteById(tagId);
  }
}
