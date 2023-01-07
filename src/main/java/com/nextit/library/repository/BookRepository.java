package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository {

    Page<Book> findAll(Pageable pageable);

    Page<Book> findAllAvailable(Pageable pageable);

    Page<Book> findAllBorrowed(Pageable pageable);

    Book save(Book entity);

    boolean existsById(int id);
}
