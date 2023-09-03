package com.book.library.book;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookRestController extends AbstractBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookRestController.class);
    private static final String MESSAGE = "Book %s failed. Book with id='%d' not found.";

    private final ObservationRegistry registry;

    public BookRestController(@Autowired BookService bookService, @Autowired ObservationRegistry registry) {
        super(bookService);
        this.registry = registry;
    }

    @GetMapping("/all")
    public List<AnyBookDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getService().findAllBooks(page).toList();
    }

    @GetMapping("/available")
    public List<AvailableBookDto> available(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getService().findAllAvailableBooks(page).toList();
    }

    @GetMapping("/borrowed")
    public List<BorrowedBookDto> borrowed(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getService().findAllBorrowedBooks(page).toList();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public AvailableBookDto add(@Valid @RequestBody AvailableBookDto dto) {
        return Observation.createNotStarted("addition.book", this.registry).observe(() -> updateBook(dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    public AvailableBookDto update(@Valid @RequestBody AvailableBookDto dto) {
        int id = dto.getId();
        if (!getService().bookExists(id)) {
            handleBookNotFound("updating", id);
        }

        return observe("updating.book.id", () -> updateBook(dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete")
    public void delete(@RequestParam("bookId") int id) {
        if (!getService().bookExists(id)) {
            handleBookNotFound("deletion", id);
        }

        Observation.createNotStarted("deletion.book.id", this.registry).observe(() -> deleteBook(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/avail")
    public AvailableBookDto avail(@RequestParam("bookId") int id) {
        if (!getService().bookExists(id)) {
            handleBookNotFound("availing", id);
        }

        return observe("availing.book.id", () -> availBook(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/borrow")
    public BorrowedBookDto borrow(@Valid @RequestBody BorrowedDto dto) {
        int bookId = dto.getBookId();
        if (!getService().bookExists(bookId)) {
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
        return Observation.createNotStarted(registryName, this.registry).observe(supplier);
    }
}
