package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.dto.AvailableBookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

abstract class AbstractBookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBookController.class);

    private final BookService service;
    private final BookMapper mapper;

    AbstractBookController(BookService service, BookMapper mapper) {
        this.service = Objects.requireNonNull(service);
        this.mapper = Objects.requireNonNull(mapper);
    }

    BookService getService() {
        return service;
    }

    BookMapper getMapper() {
        return mapper;
    }

    Book addBook(AvailableBookDto dto) {
        Book book = mapper.toEntity(dto);
        Book newBook = service.save(book);

        String message = "\"%s\" added into repository.".formatted(newBook.toString());
        LOGGER.info(message);

        return newBook;
    }

    void deleteBook(int id) {
        service.deleteById(id);

        String message = "Book with id='%d' deleted successfully.".formatted(id);
        LOGGER.info(message);
    }
}
