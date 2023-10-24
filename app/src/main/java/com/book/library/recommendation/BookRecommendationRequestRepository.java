package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.reader.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecommendationRequestRepository extends JpaRepository<BookRecommendationRequest, Integer> {

    boolean existsByBookAndRecommenced(Book book, Reader recommenced);

    BookRecommendationRequest findByBookIdAndRecommencedId(int bookId, int recommencedId);
}
