package com.book.library.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Integer> {

    @Query(
            """
            SELECT new com.book.library.book.BorrowedBookDto(bb.id, bb.borrowedOn, b.name, b.author, r.firstName, r.lastName)
            FROM BorrowedBook bb
            JOIN bb.book b
            JOIN bb.reader r""")
    Page<BorrowedBookDto> findAllBorrowedBooks(Pageable pageable);
}
