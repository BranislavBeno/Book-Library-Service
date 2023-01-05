package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDbRepository extends JpaRepository<Book, Long> {
}
