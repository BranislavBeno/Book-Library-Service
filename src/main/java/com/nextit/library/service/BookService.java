package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;

public record BookService(BookRepository repository) {

    public Iterable<Book> findAll() {
        return repository.findAll();
    }
}
