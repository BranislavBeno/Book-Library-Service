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
    private static final String MESSAGE = "Book %s failed. Book with id='%d' not found.";

    public BookRestController(@Autowired BookService bookService,
                              @Autowired BookMapper mapper) {
        super(bookService, mapper);
    }

    @GetMapping("/all")
    public List<AnyBookDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return provideDtoList(getService().findAll(page), b -> getMapper().toAnyBookDto(b));
    }

    @GetMapping("/available")
    public List<AvailableBookDto> available(@RequestParam(name = "page", defaultValue = "0") int page) {
        return provideDtoList(getService().findAllAvailable(page), b -> getMapper().toAvailableBookDto(b));
    }

    @GetMapping("/borrowed")
    public List<BorrowedBookDto> borrowed(@RequestParam(name = "page", defaultValue = "0") int page) {
        return provideDtoList(getService().findAllBorrowed(page), b -> getMapper().toBorrowedBookDto(b));
    }

    @PostMapping("/add")
    public ResponseEntity<AvailableBookDto> add(@Valid @RequestBody AvailableBookDto dto) {
        AvailableBookDto bookDto = updateBook(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bookDto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(bookDto);
    }

    @PutMapping("/update")
    public ResponseEntity<AvailableBookDto> update(@Valid @RequestBody AvailableBookDto dto) {
        if (getService().existsById(dto.getId())) {
            AvailableBookDto bookDto = updateBook(dto);

            return ResponseEntity.ok(bookDto);
        } else {
            String message = "Book '%s' not found.".formatted(dto.getName());
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(@RequestParam("bookId") int id) {
        if (getService().existsById(id)) {
            deleteBook(id);

            return ResponseEntity.noContent().build();
        } else {
            String message = MESSAGE.formatted("'deletion'", id);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/avail")
    public ResponseEntity<AvailableBookDto> avail(@RequestParam("bookId") int id) {
        if (getService().existsById(id)) {
            AvailableBookDto bookDto = availBook(id);

            return ResponseEntity.ok(bookDto);
        } else {
            String message = MESSAGE.formatted("'availing'", id);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/borrow")
    public ResponseEntity<BorrowedBookDto> borrow(@Valid @RequestBody BorrowedDto dto) {
        int bookId = dto.bookId();
        if (getService().existsById(bookId)) {
            Book updated = borrowBook(dto);

            String message = "\"%s\" borrowed successfully.".formatted(updated.toString());
            LOGGER.info(message);

            return ResponseEntity.ok(getMapper().toBorrowedBookDto(updated));
        } else {
            String message = MESSAGE.formatted("'borrowing'", bookId);
            LOGGER.error(message);
            return ResponseEntity.badRequest().build();
        }
    }

    private Book borrowBook(BorrowedDto dto) {
        Book book = getService().findById(dto.bookId());
        book.setBorrowed(new Borrowed(dto.firstName(), dto.lastName(), dto.from()));

        return getService().save(book);
    }
}
