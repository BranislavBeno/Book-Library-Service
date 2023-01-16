package com.nextit.library.controller;

import com.nextit.library.dto.*;
import com.nextit.library.service.BookService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/v1/books")
public class BookRestController extends AbstractBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRestController.class);
    private static final String MESSAGE = "Book %s failed. Book with id='%d' not found.";

    private final ObservationRegistry registry;

    public BookRestController(@Autowired BookService bookService,
                              @Autowired BookMapper mapper,
                              @Autowired ObservationRegistry registry) {
        super(bookService, mapper);
        this.registry = registry;
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
    public AvailableBookDto add(@Valid @RequestBody AvailableBookDto dto) {
        return Observation.createNotStarted("addition.book", this.registry)
                .observe(() -> updateBook(dto));
    }

    @PutMapping("/update")
    public AvailableBookDto update(@Valid @RequestBody AvailableBookDto dto) {
        int id = dto.getId();
        if (!getService().existsById(id)) {
            handleBookNotFound("updating", id);
        }

        return observe("updating.book.id", () -> updateBook(dto));
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("bookId") int id) {
        if (!getService().existsById(id)) {
            handleBookNotFound("deletion", id);
        }

        Observation.createNotStarted("deletion.book.id", this.registry)
                .observe(() -> deleteBook(id));
    }

    @PutMapping("/avail")
    public AvailableBookDto avail(@RequestParam("bookId") int id) {
        if (!getService().existsById(id)) {
            handleBookNotFound("availing", id);
        }

        return observe("availing.book.id", () -> availBook(id));
    }

    @PutMapping("/borrow")
    public BorrowedBookDto borrow(@Valid @RequestBody BorrowedDto dto) {
        int bookId = dto.getBookId();
        if (!getService().existsById(bookId)) {
            handleBookNotFound("borrowing", bookId);
        }

        return observe("borrowing.book.id", () -> borrowBook(dto));
    }

    private static void handleBookNotFound(String operation, int id) {
        String message = MESSAGE.formatted(operation, id);
        LOGGER.error(message);
        throw new BookNotFoundException(message);
    }

    private <T> T observe(String registryName, Supplier<T> supplier) {
        return Observation.createNotStarted(registryName, this.registry)
                .observe(supplier);
    }
}
