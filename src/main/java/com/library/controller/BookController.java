package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookController {

  private final BookRepository bookRepository;

  @GetMapping("/books")
  public ResponseEntity<List<Book>> getAllBooks() {
    List<Book> Books = new ArrayList<>(bookRepository.findAll());

    if (Books.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return ResponseEntity.ok(Books);
  }

  @GetMapping("/books/{id}")
  public ResponseEntity<Book> getBookById(@PathVariable("id") long id) {
    Optional<Book> optionalBook = bookRepository.findById(id);
    return ResponseEntity.of(optionalBook);
  }

  @PostMapping("/books")
  public ResponseEntity<Book> createBook(@RequestBody @Valid Book book) {
    Book persistedBook = bookRepository
        .save(Book.builder().title(book.getTitle()).build());
    return ResponseEntity.status(HttpStatus.CREATED).body(persistedBook);
  }

  @PutMapping("/books/{id}")
  public ResponseEntity<Book> updateBook(@PathVariable("id") long id,
      @RequestBody Book book) {
    Optional<Book> optionalBook = bookRepository.findById(id);
    if (optionalBook.isPresent()) {
      Book updateBook = optionalBook.get();
      updateBook.setTitle(book.getTitle());
      return ResponseEntity.ok(bookRepository.save(updateBook));
    } else {
      return ResponseEntity.noContent().build();
    }
  }

  @DeleteMapping("/books/{id}")
  public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") long id) {
    bookRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/books")
  public ResponseEntity<HttpStatus> deleteAllBooks() {
    bookRepository.deleteAll();
    return ResponseEntity.noContent().build();
  }
}
