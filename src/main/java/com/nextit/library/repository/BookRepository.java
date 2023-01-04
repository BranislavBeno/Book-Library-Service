package com.nextit.library.repository;

import com.nextit.library.domain.Book;

import java.util.List;

public interface BookRepository {

    List<Book> findAll();

    List<Book> findAllAvailable();

    List<Book> findAllBorrowed();
}
