package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import com.nextit.library.dto.AvailableBookDto;
import com.nextit.library.dto.BookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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

    <T extends BookDto> List<T> provideDtoList(Page<Book> bookPage, Function<Book, T> function) {
        return bookPage.getContent()
                .stream()
                .map(function)
                .toList();
    }

    AvailableBookDto findBook(int id) {
        Book book = service.findById(id);
        if (book != null) {
            return getMapper().toAvailableBookDto(book);
        }

        return new AvailableBookDto();
    }

    AvailableBookDto updateBook(AvailableBookDto dto) {
        Book book = service.findById(dto.getId());

        if (book != null) {
            book.setName(dto.getName());
            book.setAuthor(dto.getAuthor());
        } else {
            book = mapper.toEntity(dto);
        }

        AvailableBookDto bookDto = mapper.toAvailableBookDto(service.save(book));

        String message = "\"%s\" saved into repository.".formatted(bookDto.toString());
        LOGGER.info(message);

        return bookDto;
    }

    AvailableBookDto availBook(int id) {
        Book book = service.findById(id);
        book.setBorrowed(new Borrowed());

        AvailableBookDto bookDto = mapper.toAvailableBookDto(service.save(book));

        String message = "\"%s\" made available successfully.".formatted(bookDto.toString());
        LOGGER.info(message);

        return bookDto;
    }

    void deleteBook(int id) {
        service.deleteById(id);

        String message = "Book with id='%d' deleted successfully.".formatted(id);
        LOGGER.info(message);
    }
}
