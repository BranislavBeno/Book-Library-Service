package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record BookService(BookRepository repository, int pageSize) {

    public Page<Book> findAll(int page) {
        return repository.findAll(getPageRequest(page));
    }

    public Page<Book> findAllAvailable(int page) {
        return repository.findAllAvailable(getPageRequest(page));
    }

    public Page<Book> findAllBorrowed(int page) {
        return repository.findAllBorrowed(getPageRequest(page));
    }

    public Book save(Book book) {
        return repository.save(book);
    }

    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    public Book findById(int id) {
        return repository.findById(id).orElse(null);
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
