package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepository {

    Page<Book> findAll(Pageable pageable);

    List<Book> findAllAvailable();

    List<Book> findAllBorrowed();
}
