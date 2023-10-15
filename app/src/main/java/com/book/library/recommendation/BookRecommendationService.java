package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.book.BookRepository;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record BookRecommendationService(
        BorrowedBookRepository borrowedBookRepository,
        BookRepository bookRepository,
        ReaderRepository readerRepository,
        BookRecommendationRequestRepository requestRepository,
        SqsTemplate sqsTemplate,
        String bookRecommendationQueueName) {

    private static final Logger LOG = LoggerFactory.getLogger(BookRecommendationService.class);
    private static final String INVALID_BOOK_ID = "Invalid book id: ";
    private static final String INVALID_READER_ID = "Invalid reader id: ";

    public String recommendBookTo(int bookId, int readerId) {
        RequestParams params = getRequestParams(bookId, readerId);
        Book book = params.book;
        Reader reader = params.reader;

        if (requestRepository.existsByBookAndRecommenced(book, reader)) {
            String message = "Recommendation request for book %s with recommenced %s already exists"
                    .formatted(book.bookInfo(), reader.fullName());
            LOG.info(message);

            return reader.fullName();
        }

        LOG.info("About to recommend book with id {} to reader with id {}", bookId, readerId);

        BookRecommendationRequest recommendation = new BookRecommendationRequest();
        String token = UUID.randomUUID().toString();
        recommendation.setToken(token);
        recommendation.setRecommenced(reader);
        recommendation.setBook(book);
        book.getRecommendationRequests().add(recommendation);

        requestRepository.save(recommendation);

        sqsTemplate.send(bookRecommendationQueueName, new BookRecommendationNotification(recommendation));

        return reader.fullName();
    }

    private RequestParams getRequestParams(int borrowedBookId, int readerId) {
        Reader reader = readerRepository
                .findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_READER_ID + readerId));
        Book book = borrowedBookRepository
                .findRecommendedBookByBorrowedBookId(borrowedBookId)
                .flatMap(b -> bookRepository.findBookById(b.bookId()))
                .orElseThrow(() -> new IllegalArgumentException(INVALID_BOOK_ID + borrowedBookId));

        return new RequestParams(book, reader);
    }

    private record RequestParams(Book book, Reader reader) {}
}
