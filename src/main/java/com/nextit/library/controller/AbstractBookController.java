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

    private final BookService bookService;
    private final BookMapper mapper;

    AbstractBookController(BookService bookService, BookMapper mapper) {
        this.bookService = Objects.requireNonNull(bookService);
        this.mapper = Objects.requireNonNull(mapper);
    }

    BookService getBookService() {
        return bookService;
    }

    BookMapper getMapper() {
        return mapper;
    }

    Book addBook(AvailableBookDto dto) {
        Book book = getMapper().toEntity(dto);
        Book newBook = getBookService().save(book);

        String message = "\"%s\" added into repository.".formatted(newBook.toString());
        LOGGER.info(message);

        return newBook;
    }
}
