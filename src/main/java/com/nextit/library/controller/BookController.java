package com.nextit.library.controller;

import com.nextit.library.domain.Book;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
        PageData pageData = providePageData(page, bookPage, mapper::toAnyDto);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "index";
    }

    @GetMapping("/available")
    public String showAvailable(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = bookService.findAllAvailable(page);
        PageData pageData = providePageData(page, bookPage, mapper::toAvailableDto);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "available-books";
    }

    @GetMapping("/borrowed")
    public String showBorrowed(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = bookService.findAllBorrowed(page);
        PageData pageData = providePageData(page, bookPage, mapper::toBorrowedDto);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "borrowed-books";
    }

    private Page<BookDto> provideDtoPage(int page, Page<Book> bookPage, Function<Book, BookDto> function) {
        List<BookDto> content = bookPage.getContent()
                .stream()
                .map(function)
                .toList();

        return new PageImpl<>(content, PageRequest.of(page, bookService.pageSize()), bookPage.getTotalPages());
    }

    private List<Integer> providePageNumbers(int totalPages) {
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
        }

        return Collections.emptyList();
    }

    private PageData providePageData(int page, Page<Book> bookPage, Function<Book, BookDto> function) {
        Page<BookDto> dtoPage = provideDtoPage(page, bookPage, function);
        List<Integer> pageNumbers = providePageNumbers(bookPage.getTotalPages());

        return new PageData(dtoPage, pageNumbers);
    }

    private record PageData(Page<BookDto> dtoPage, List<Integer> pageNumbers) {
    }
}
