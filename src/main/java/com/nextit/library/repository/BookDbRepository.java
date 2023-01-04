package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookDbRepository extends CrudRepository<Book, Long> {
}
