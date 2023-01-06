package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.dto.AnyBookDto;
import com.nextit.library.dto.AvailableBookDto;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.dto.BorrowedBookDto;
import com.nextit.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping("/add")
    public ResponseEntity<AvailableBookDto> add(@Valid @RequestBody AvailableBookDto dto) {
        Book book = getMapper().toEntity(dto);
        Book customer = getBookService().save(book);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();

        return ResponseEntity.created(uri).body(getMapper().toAvailableDto(customer));
    }
}
