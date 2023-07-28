package com.book.library.repository;

import com.book.library.domain.FileBook;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookFileRepository {

    Page<FileBook> findAll(Pageable pageable);

    Page<FileBook> findAllAvailable(Pageable pageable);

    Page<FileBook> findAllBorrowed(Pageable pageable);

    FileBook save(FileBook entity);

    boolean existsById(int id);

    Optional<FileBook> findById(int id);

    void deleteById(int id);
}
