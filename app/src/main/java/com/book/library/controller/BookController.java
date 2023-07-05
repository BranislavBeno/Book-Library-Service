package com.book.library.controller;

import com.book.library.domain.Book;
import com.book.library.dto.AvailableBookDto;
import com.book.library.dto.BookDto;
import com.book.library.dto.BookMapper;
import com.book.library.dto.BorrowedDto;
import com.book.library.service.BookService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController extends AbstractBookController {

    private static final String FOUND_ATTR = "found";
    private static final String BOOKS_ATTR = "books";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";
    private static final String SAVE_BOOK_PAGE = "save-book";

    public BookController(@Autowired BookService service, @Autowired BookMapper mapper) {
        super(service, mapper);
    }

    @GetMapping("/")
    public String showAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal OidcUser user) {
        Page<Book> bookPage = getService().findAll(page);
        PageData pageData = providePageData(page, bookPage, b -> getMapper().toAnyBookDto(b));

        model.addAttribute("userName", user.getFullName());
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

        return SAVE_BOOK_PAGE;
    }

    @GetMapping("/updateBook")
    public String updateBook(@RequestParam("bookId") int id, Model model) {
        model.addAttribute("availableBookDto", findBook(id));

        return SAVE_BOOK_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save")
    public String save(@Valid AvailableBookDto availableBookDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SAVE_BOOK_PAGE;
        }

        updateBook(availableBookDto);

        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete")
    public String delete(@RequestParam("bookId") int id) {
        deleteBook(id);

        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/avail")
    public String avail(@RequestParam("bookId") int id) {
        availBook(id);

        return "redirect:/borrowed";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/borrowBook")
    public String borrowBook(@RequestParam("bookId") int id, Model model) {
        BorrowedDto borrowedDto = new BorrowedDto();
        borrowedDto.setBookId(id);
        model.addAttribute("borrowedDto", borrowedDto);

        return "borrow-book";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/borrow")
    public String borrow(@Valid BorrowedDto borrowedDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "borrow-book";
        }

        borrowBook(borrowedDto);

        return "redirect:/available";
    }

    private Page<BookDto> provideDtoPage(int page, Page<Book> bookPage, Function<Book, BookDto> function) {
        List<BookDto> content = provideDtoList(bookPage, function);
        return new PageImpl<>(content, PageRequest.of(page, getService().pageSize()), bookPage.getTotalPages());
    }

    private List<Integer> providePageNumbers(int totalPages) {
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages).boxed().toList();
        }

        return Collections.emptyList();
    }

    private PageData providePageData(int page, Page<Book> bookPage, Function<Book, BookDto> function) {
        Page<BookDto> dtoPage = provideDtoPage(page, bookPage, function);
        List<Integer> pageNumbers = providePageNumbers(bookPage.getTotalPages());

        return new PageData(dtoPage, pageNumbers);
    }

    private record PageData(Page<BookDto> dtoPage, List<Integer> pageNumbers) {}
}
