package com.book.library.repository;

import com.book.library.domain.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookFileRepository {

    Page<Book> findAll(Pageable pageable);

    Page<Book> findAllAvailable(Pageable pageable);

    Page<Book> findAllBorrowed(Pageable pageable);

    Book save(Book entity);

    boolean existsById(int id);

    Optional<Book> findById(int id);

    void deleteById(int id);
}
