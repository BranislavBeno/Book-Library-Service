package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.dto.BookAvailableDto;
import com.nextit.library.dto.BookBorrowedDto;
import com.nextit.library.dto.BookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Controller
public final class BookController {

    private static final String FOUND_ATTR = "found";
    private static final String BOOKS_ATTR = "books";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";
    private final BookService bookService;
    private final BookMapper mapper;

    public BookController(@Autowired BookService bookService,
                          @Autowired BookMapper mapper) {
        this.bookService = Objects.requireNonNull(bookService);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @GetMapping("/")
    public String showAll(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = bookService.findAll(page);

        List<BookDto> content = bookPage.getContent()
                .stream()
                .map(mapper::toBookDto)
                .toList();
        int totalPages = bookPage.getTotalPages();
        Page<BookDto> dtoPage = new PageImpl<>(content, PageRequest.of(page, bookService.pageSize()), totalPages);

        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute(PAGE_NUMBERS_ATTR, pageNumbers);
        }

        model.addAttribute(FOUND_ATTR, !content.isEmpty());
        model.addAttribute(BOOKS_ATTR, dtoPage);

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
