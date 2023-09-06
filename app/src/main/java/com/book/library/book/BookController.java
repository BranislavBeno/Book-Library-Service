package com.book.library.book;

import com.book.library.dto.AnyBookDto;
import com.book.library.dto.AvailableBookDto;
import com.book.library.dto.BorrowedBookDto;
import com.book.library.dto.DataTransferObject;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookController extends AbstractBookController {

    private static final String FORBIDDEN_ATTR = "forbidden";
    private static final String FOUND_ATTR = "found";
    private static final String BOOKS_ATTR = "books";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";
    private static final String SAVE_BOOK_PAGE = "save-book";

    public BookController(@Autowired BookService service) {
        super(service);
    }

    @ModelAttribute(FORBIDDEN_ATTR)
    public boolean defaultForbidden() {
        return false;
    }

    @GetMapping("/books")
    public String showBooks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model,
            @ModelAttribute(FORBIDDEN_ATTR) boolean forbidden) {
        Page<AnyBookDto> bookPage = getService().findBooks(page);
        PageData<AnyBookDto> pageData = providePageData(bookPage);

        model.addAttribute(FORBIDDEN_ATTR, forbidden);
        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "all-books";
    }

    @GetMapping("/available")
    public String showAvailableBooks(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<AvailableBookDto> bookPage = getService().findAvailableBooks(page);
        PageData<AvailableBookDto> pageData = providePageData(bookPage);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "available-books";
    }

    @GetMapping("/borrowed")
    public String showBorrowedBooks(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<BorrowedBookDto> bookPage = getService().findBorrowedBooks(page);
        PageData<BorrowedBookDto> pageData = providePageData(bookPage);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "borrowed-books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/addBook")
    public String addBook(Model model) {
        model.addAttribute("availableBookDto", new AvailableBookDto());

        return SAVE_BOOK_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

        return "redirect:/books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete")
    public String delete(@RequestParam("bookId") int id, RedirectAttributes attributes) {
        try {
            deleteBook(id);
            attributes.addFlashAttribute(FORBIDDEN_ATTR, false);
        } catch (BookDeletionException e) {
            attributes.addFlashAttribute(FORBIDDEN_ATTR, true);
        }

        return "redirect:/books";
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

    private List<Integer> providePageNumbers(int totalPages) {
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages).boxed().toList();
        }

        return Collections.emptyList();
    }

    private <T extends DataTransferObject> PageData<T> providePageData(Page<T> page) {
        List<Integer> pageNumbers = providePageNumbers(page.getTotalPages());

        return new PageData<>(page, pageNumbers);
    }

    private record PageData<T extends DataTransferObject>(Page<T> dtoPage, List<Integer> pageNumbers) {}
}
