package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;

import java.util.List;

public record BookService(BookRepository repository) {

    public List<Book> findAll() {
        return List.copyOf(repository.findAll());
    }

    public List<Book> findAllAvailable() {
        return List.copyOf(repository.findAllAvailable());
    }

    public List<Book> findAllBorrowed() {
        return List.copyOf(repository.findAllBorrowed());
    }
}
