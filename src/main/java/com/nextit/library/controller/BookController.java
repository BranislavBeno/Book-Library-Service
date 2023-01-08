package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.dto.AvailableBookDto;
import com.nextit.library.dto.BookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Controller
public final class BookController extends AbstractBookController {

    private static final String FOUND_ATTR = "found";
    private static final String BOOKS_ATTR = "books";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";

    public BookController(@Autowired BookService service,
                          @Autowired BookMapper mapper) {
        super(service, mapper);
    }

    @GetMapping("/")
    public String showAll(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = getService().findAll(page);
        PageData pageData = providePageData(page, bookPage, b -> getMapper().toAnyBookDto(b));

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "index";
    }

    @GetMapping("/available")
    public String showAvailable(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = getService().findAllAvailable(page);
        PageData pageData = providePageData(page, bookPage, b -> getMapper().toAvailableBookDto(b));

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "available-books";
    }

    @GetMapping("/borrowed")
    public String showBorrowed(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<Book> bookPage = getService().findAllBorrowed(page);
        PageData pageData = providePageData(page, bookPage, b -> getMapper().toBorrowedBookDto(b));

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "borrowed-books";
    }

    @GetMapping("/addBook")
    public String addBook(Model model) {
        model.addAttribute("availableBookDto", new AvailableBookDto());

        return "save-book";
    }

    @GetMapping("/updateBook")
    public String updateBook(@RequestParam("bookId") int id, Model model) {
        model.addAttribute("availableBookDto", findBook(id));

        return "save-book";
    }

    @PostMapping("/save")
    public String save(@Valid AvailableBookDto availableBookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "save-book";
        }

        updateBook(availableBookDto);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        deleteBook(id);

        return "redirect:/";
    }

    private Page<BookDto> provideDtoPage(int page, Page<Book> bookPage, Function<Book, BookDto> function) {
        List<BookDto> content = provideDtoList(bookPage, function);
        return new PageImpl<>(content, PageRequest.of(page, getService().pageSize()), bookPage.getTotalPages());
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
