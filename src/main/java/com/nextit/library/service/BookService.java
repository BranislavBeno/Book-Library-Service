package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public record BookService(BookRepository repository, int pageSize) {

    public Page<Book> findAll(int page) {
        return repository.findAll(getPageRequest(page));
    }

    public Page<Book> findAllAvailable(int page) {
        return repository.findAllAvailable(getPageRequest(page));
    }

    public List<Book> findAllBorrowed() {
        return List.copyOf(repository.findAllBorrowed());
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
