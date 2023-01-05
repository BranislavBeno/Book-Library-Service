package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public record BookService(BookRepository repository, int pageSize) {

    public Page<Book> findAll(int currentPage) {
        return repository.findAll(PageRequest.of(currentPage, pageSize));
    }

    public List<Book> findAllAvailable() {
        return List.copyOf(repository.findAllAvailable());
    }

    public List<Book> findAllBorrowed() {
        return List.copyOf(repository.findAllBorrowed());
    }
}
