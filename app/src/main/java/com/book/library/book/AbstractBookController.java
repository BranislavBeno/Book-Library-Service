package com.book.library.book;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

abstract class AbstractBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBookController.class);

    private final BookService service;

    AbstractBookController(BookService service) {
        this.service = Objects.requireNonNull(service);
    }

    BookService getService() {
        return service;
    }

    AvailableBookDto findBook(long id) {
        Book book = service.findBook(id);

        return new AvailableBookDto(Objects.requireNonNullElseGet(book, Book::new));
    }

    AvailableBookDto updateBook(AvailableBookDto dto) {
        Book book = service.findBook(dto.id());

        if (book != null) {
            book.setName(dto.name());
            book.setAuthor(dto.author());
        } else {
            book = toBookEntity(dto);
        }

        AvailableBookDto bookDto = new AvailableBookDto(service.saveBook(book));

        String message = "%s saved into repository.".formatted(book.toString());
        LOGGER.info(message);

        return bookDto;
    }

    void deleteBook(long id) {
        try {
            service.deleteBook(id);
            String message = "Book with id='%d' deleted successfully.".formatted(id);
            LOGGER.info(message);
        } catch (DataIntegrityViolationException e) {
            String message = "Book with id='%d' can't be deleted due to is still borrowed.".formatted(id);
            LOGGER.error(message);
            throw new BookDeletionException(message);
        }
    }

    AvailableBookDto availBook(long id) {
        AvailableBookDto bookDto = service.availBook(id);

        String message = bookDto != null
                ? "%s made available successfully.".formatted(bookDto.toString())
                : "Book with id=%d not found".formatted(id);
        LOGGER.info(message);

        return bookDto;
    }

    BorrowedBookDto borrowBook(BorrowedDto dto) {
        BorrowedBookDto bookDto = service.borrowBook(dto.bookId(), dto.readerId());

        String message = bookDto != null
                ? "%s borrowed successfully.".formatted(bookDto.toString())
                : "Book with id=%d not found".formatted(dto.bookId());

        LOGGER.info(message);

        return bookDto;
    }

    private Book toBookEntity(AvailableBookDto dto) {
        var book = new Book();
        book.setId(dto.id());
        book.setName(dto.name());
        book.setAuthor(dto.author());

        return book;
    }
}
