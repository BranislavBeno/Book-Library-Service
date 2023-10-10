package com.book.library.recommendation;

import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.ReaderRepository;

public record BookRecommendationService(BorrowedBookRepository bookRepository, ReaderRepository readerRepository) {

    public String recommendBookTo(Long bookId, Long readerId) {
        return null;
    }
}
