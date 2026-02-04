package com.book.library.book;

import com.book.library.controller.ViewController;
import com.book.library.dto.*;
import com.book.library.tracing.TracingEvent;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookController extends AbstractBookController implements ViewController {

    private static final Logger LOG = LoggerFactory.getLogger(BookController.class);
    private static final String FORBIDDEN_ATTR = "forbidden";
    private static final String FOUND_ATTR = "found";
    private static final String BOOKS_ATTR = "books";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";
    private static final String SAVE_BOOK_PAGE = "save-book";

    private final ApplicationEventPublisher publisher;

    public BookController(BookService service, ApplicationEventPublisher publisher) {
        super(service);
        this.publisher = publisher;
    }

    @ModelAttribute(FORBIDDEN_ATTR)
    public boolean defaultForbidden() {
        return false;
    }

    @GetMapping("/all")
    public String showBooks(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @ModelAttribute(FORBIDDEN_ATTR) boolean forbidden,
            Principal principal) {
        Page<AnyBookDto> bookPage = getService().findBooks(page);
        PageData<AnyBookDto> pageData = providePageData(bookPage);

        model.addAttribute(FORBIDDEN_ATTR, forbidden);
        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        TracingEvent event = new TracingEvent(this, "/book/all", principal != null ? principal.getName() : "anonymous");
        publisher.publishEvent(event);
        LOG.info("Successfully published event");

        return "all-books";
    }

    @GetMapping("/available")
    public String showAvailableBooks(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<AvailableBookDto> bookPage = getService().findAvailableBooks(page);
        PageData<AvailableBookDto> pageData = providePageData(bookPage);

        model.addAttribute(FOUND_ATTR, !bookPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "available-books";
    }

    @GetMapping("/borrowed")
    public String showBorrowedBooks(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<BorrowedBookDto> bookPage = getService().findBorrowedBooks(page);
        Page<BorrowedBookExtendedDto> extendedPage = extendPage(bookPage);
        PageData<BorrowedBookExtendedDto> pageData = providePageData(extendedPage);

        model.addAttribute(FOUND_ATTR, !extendedPage.isEmpty());
        model.addAttribute(BOOKS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "borrowed-books";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/show-add")
    public String addBook(Model model) {
        model.addAttribute("availableBookDto", new AvailableBookDto());

        return SAVE_BOOK_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/show-update")
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

        return "redirect:/book/all";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/delete")
    public String delete(@RequestParam("bookId") int id, RedirectAttributes attributes) {
        try {
            deleteBook(id);
            attributes.addFlashAttribute(FORBIDDEN_ATTR, false);
        } catch (BookDeletionException _) {
            attributes.addFlashAttribute(FORBIDDEN_ATTR, true);
        }

        return "redirect:/book/all";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/avail")
    public String avail(@RequestParam("bookId") int id) {
        availBook(id);

        return "redirect:/book/borrowed";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/show-borrow")
    public String borrowBook(@RequestParam int bookId, @RequestParam(defaultValue = "1") int readerId, Model model) {
        BorrowedDto borrowedDto = new BorrowedDto(bookId, readerId);

        return callBorrowTemplate(model, borrowedDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/borrow")
    public String borrow(@Valid BorrowedDto borrowedDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return callBorrowTemplate(model, borrowedDto);
        }

        borrowBook(borrowedDto);

        return "redirect:/book/available";
    }

    private String callBorrowTemplate(Model model, BorrowedDto borrowedDto) {
        List<ReaderDto> readers = getService().findAllReaders();

        model.addAttribute(FOUND_ATTR, !readers.isEmpty());
        model.addAttribute("readers", readers);
        model.addAttribute("borrowedDto", borrowedDto);

        return "borrow-book";
    }

    private Page<BorrowedBookExtendedDto> extendPage(Page<BorrowedBookDto> bookPage) {
        List<ReaderDto> readers = getService().findAllReaders();
        List<BorrowedBookExtendedDto> list = bookPage.getContent().stream()
                .map(b -> {
                    List<ReaderDto> candidates = readers.stream()
                            .filter(r -> r.getId() != b.readerId())
                            .toList();
                    return new BorrowedBookExtendedDto(b, candidates);
                })
                .toList();

        return new PageImpl<>(list, bookPage.getPageable(), bookPage.getTotalElements());
    }
}
