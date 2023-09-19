package com.book.library.reader;

import com.book.library.controller.ViewController;
import com.book.library.dto.ReaderDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/reader")
public class ReaderController extends AbstractReaderController implements ViewController {

    private static final String FORBIDDEN_ATTR = "forbidden";
    private static final String FOUND_ATTR = "found";
    private static final String READERS_ATTR = "readers";
    private static final String PAGE_NUMBERS_ATTR = "pageNumbers";
    private static final String SAVE_READER_PAGE = "save-reader";

    ReaderController(@Autowired ReaderService service) {
        super(service);
    }

    @ModelAttribute(FORBIDDEN_ATTR)
    public boolean defaultForbidden() {
        return false;
    }

    @GetMapping("/all")
    public String showReaders(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Page<ReaderDto> readerPage = getService().findAllReaders(page);
        PageData<ReaderDto> pageData = providePageData(readerPage);

        model.addAttribute(FOUND_ATTR, !readerPage.isEmpty());
        model.addAttribute(READERS_ATTR, pageData.dtoPage());
        model.addAttribute(PAGE_NUMBERS_ATTR, pageData.pageNumbers());

        return "all-readers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/show-add")
    public String addReader(Model model) {
        model.addAttribute("readerDto", new ReaderDto());

        return SAVE_READER_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save")
    public String save(@Valid ReaderDto readerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SAVE_READER_PAGE;
        }

        updateReader(readerDto);

        return "redirect:/reader/all";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("readerId") int id, RedirectAttributes attributes) {
        try {
            deleteReader(id);
            attributes.addFlashAttribute(FORBIDDEN_ATTR, false);
        } catch (ReaderDeletionException e) {
            attributes.addFlashAttribute(FORBIDDEN_ATTR, true);
        }

        return "redirect:/reader/all";
    }
}
