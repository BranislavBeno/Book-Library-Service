package com.nextit.library.controller;

import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;

import java.util.Objects;

abstract class AbstractBookController {

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
}
