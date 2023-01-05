package com.nextit.library.controller;

import com.nextit.library.dto.BookAvailableDto;
import com.nextit.library.dto.BookBorrowedDto;
import com.nextit.library.dto.BookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;

@Controller
public final class BookController {

    public static final String FOUND_ATTR = "found";
    public static final String BOOKS_ATTR = "books";
    private final BookService bookService;
    private final BookMapper mapper;

    public BookController(@Autowired BookService bookService,
                          @Autowired BookMapper mapper) {
        this.bookService = Objects.requireNonNull(bookService);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @GetMapping("/")
    public String showAll(Model model) {
        List<BookDto> books = bookService.findAll().stream()
                .map(mapper::toBookDto)
                .toList();

        model.addAttribute(FOUND_ATTR, !books.isEmpty());
        model.addAttribute(BOOKS_ATTR, books);

        return "index";
    }

    @GetMapping("/available")
    public String showAvailable(Model model) {
        List<BookAvailableDto> books = bookService.findAllAvailable().stream()
                .map(mapper::toAvailableDto)
                .toList();

        model.addAttribute(FOUND_ATTR, !books.isEmpty());
        model.addAttribute(BOOKS_ATTR, books);

        return "available-books";
    }

    @GetMapping("/borrowed")
    public String showBorrowed(Model model) {
        List<BookBorrowedDto> books = bookService.findAllBorrowed().stream()
                .map(mapper::toBorrowedDto)
                .toList();

        model.addAttribute(FOUND_ATTR, !books.isEmpty());
        model.addAttribute(BOOKS_ATTR, books);

        return "borrowed-books";
    }
}
