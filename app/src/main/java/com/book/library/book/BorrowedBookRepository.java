package com.book.library.book;

import com.book.library.dto.BorrowedBookDto;
import com.book.library.dto.RecommendedBookDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Integer> {

    @Query(
            """
            SELECT new com.book.library.dto.BorrowedBookDto(bb.id, bb.borrowedOn, b.name, b.author, r.id, r.firstName, r.lastName)
            FROM BorrowedBook bb
            JOIN bb.book b
            JOIN bb.reader r""")
    Page<BorrowedBookDto> findAllBorrowedBooks(Pageable pageable);

    @Query(
            """
            SELECT new com.book.library.dto.RecommendedBookDto(b.name, b.author, r.firstName, r.lastName, r.email)
            FROM BorrowedBook bb
            JOIN bb.reader r
            JOIN bb.book b WHERE bb.id = :id""")
    Optional<RecommendedBookDto> findRecommendedBookByBorrowedBookId(long id);
}
