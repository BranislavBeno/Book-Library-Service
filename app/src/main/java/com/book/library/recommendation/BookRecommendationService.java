package com.book.library.recommendation;

import com.book.library.book.BorrowedBookRepository;
import com.book.library.dto.ReaderDto;
import com.book.library.dto.RecommendedBookDto;
import com.book.library.reader.ReaderRepository;

public record BookRecommendationService(BorrowedBookRepository bookRepository, ReaderRepository readerRepository) {

    private static final String INVALID_BOOK_ID = "Invalid book id: ";
    private static final String INVALID_READER_ID = "Invalid reader id: ";

    public String recommendBookTo(Long bookId, Long readerId) {
        RecommendedBookDto bookDto = bookRepository
                .findRecommendedBookByBorrowedBookId(bookId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_BOOK_ID + bookId));

        ReaderDto readerDto = readerRepository
                .findReaderById(readerId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_READER_ID + readerId));

        return "";
    }
}
