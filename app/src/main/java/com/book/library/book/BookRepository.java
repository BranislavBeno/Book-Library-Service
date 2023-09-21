package com.book.library.book;

import com.book.library.dto.AvailableBookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(
            """
            SELECT new com.book.library.dto.AvailableBookDto(b.id, b.name, b.author)
            FROM Book b
            LEFT JOIN b.borrowed bb
            WHERE bb IS NULL""")
    Page<AvailableBookDto> findAllAvailableBooks(Pageable pageable);
}
