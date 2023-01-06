package com.nextit.library.controller;

import com.nextit.library.dto.AnyBookDto;
import com.nextit.library.dto.AvailableBookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.dto.BorrowedBookDto;
import com.nextit.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookRestController extends AbstractBookController {

    public BookRestController(@Autowired BookService bookService,
                              @Autowired BookMapper mapper) {
        super(bookService, mapper);
    }

    @GetMapping("/all")
    public List<AnyBookDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAll(page)
                .stream()
                .map(b -> getMapper().toAnyDto(b))
                .toList();
    }

    @GetMapping("/available")
    public List<AvailableBookDto> available(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAllAvailable(page)
                .stream()
                .map(b -> getMapper().toAvailableDto(b))
                .toList();
    }

    @GetMapping("/borrowed")
    public List<BorrowedBookDto> borrowed(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getBookService().findAllBorrowed(page)
                .stream()
                .map(b -> getMapper().toBorrowedDto(b))
                .toList();
    }
}
