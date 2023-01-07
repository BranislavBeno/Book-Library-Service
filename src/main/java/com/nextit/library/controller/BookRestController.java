package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import com.nextit.library.dto.*;
import com.nextit.library.service.BookService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookRestController extends AbstractBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRestController.class);
    private static final String MESSAGE = "Book with id='%d' not found.";

    public BookRestController(@Autowired BookService bookService,
                              @Autowired BookMapper mapper) {
        super(bookService, mapper);
    }

    @GetMapping("/all")
    public List<AnyBookDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAll(page)
                .stream()
                .map(b -> getMapper().toAnyBookDto(b))
                .toList();
    }

    @GetMapping("/available")
    public List<AvailableBookDto> available(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAllAvailable(page)
                .stream()
                .map(b -> getMapper().toAvailableBookDto(b))
                .toList();
    }

    @GetMapping("/borrowed")
    public List<BorrowedBookDto> borrowed(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAllBorrowed(page)
                .stream()
                .map(b -> getMapper().toBorrowedBookDto(b))
                .toList();
    }

    @PostMapping("/add")
    public ResponseEntity<AvailableBookDto> add(@Valid @RequestBody AvailableBookDto dto) {
        Book newBook = addBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newBook.getId())
                .toUri();

        return ResponseEntity.created(uri).body(getMapper().toAvailableBookDto(newBook));
    }

    @PutMapping("/update")
    public ResponseEntity<AvailableBookDto> update(@Valid @RequestBody AvailableBookDto dto) {
        if (getBookService().existsById(dto.getId())) {
            Book updated = updateBook(dto);

            String message = "\"%s\" saved successfully.".formatted(updated.toString());
            LOGGER.info(message);

            return ResponseEntity.ok(getMapper().toAvailableBookDto(updated));
        } else {
            String message = "Book '%s' not found.".formatted(dto.getName());
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        if (getBookService().existsById(id)) {
            getBookService().deleteById(id);

            String message = "Book with id='%d' deleted successfully.".formatted(id);
            LOGGER.info(message);

            return ResponseEntity.noContent().build();
        } else {
            String message = MESSAGE.formatted(id);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/avail/{id}")
    public ResponseEntity<Object> avail(@PathVariable int id) {
        if (getBookService().existsById(id)) {
            Book updated = availBook(id);

            String message = "\"%s\" made available successfully.".formatted(updated.toString());
            LOGGER.info(message);

            return ResponseEntity.ok(getMapper().toAvailableBookDto(updated));
        } else {
            String message = MESSAGE.formatted(id);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/borrow")
    public ResponseEntity<BorrowedBookDto> borrow(@Valid @RequestBody BorrowedDto dto) {
        int bookId = dto.bookId();
        if (getBookService().existsById(bookId)) {
            Book updated = borrowBook(dto);

            String message = "\"%s\" borrowed successfully.".formatted(updated.toString());
            LOGGER.info(message);

            return ResponseEntity.ok(getMapper().toBorrowedBookDto(updated));
        } else {
            String message = MESSAGE.formatted(bookId);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    private Book updateBook(AvailableBookDto dto) {
        Book book = getBookService().findById(dto.getId());
        book.setName(dto.getName());
        book.setAuthor(dto.getAuthor());

        return getBookService().save(book);
    }

    private Book availBook(int id) {
        Book book = getBookService().findById(id);
        book.setBorrowed(new Borrowed());

        return getBookService().save(book);
    }

    private Book borrowBook(BorrowedDto dto) {
        Book book = getBookService().findById(dto.bookId());
        book.setBorrowed(new Borrowed(dto.firstName(), dto.lastName(), dto.from()));

        return getBookService().save(book);
    }
}
