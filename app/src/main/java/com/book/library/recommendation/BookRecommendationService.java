package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.book.BookRepository;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.dto.RecommendedBookDto;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.ArrayList;
import java.util.Optional;
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
    private static final String INVALID_READER_EMAIL = "Invalid reader email: ";

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

        var recommendation = createBookRecommendationRequest(reader, book);
        BookRecommendationRequest request = requestRepository.save(recommendation);
        var notification = new BookRecommendationNotification(request);
        sqsTemplate.send(bookRecommendationQueueName, notification);

        return reader.fullName();
    }

    public boolean confirmRecommendation(String authenticatedReaderEmail, int bookId, int recommencedId, String token) {
        Reader recommenced = readerRepository
                .findByEmail(authenticatedReaderEmail)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_READER_EMAIL + authenticatedReaderEmail));

        if (recommenced.getId() != recommencedId) {
            return false;
        }

        BookRecommendationRequest request = requestRepository.findByBookIdAndRecommencedId(bookId, recommencedId);

        LOG.info("Recommendation request: {}", request);

        if (request == null || !request.getToken().equals(token)) {
            return false;
        }

        LOG.info("Original recommendation token: {}", request.getToken());
        LOG.info("Request token: {}", token);

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_BOOK_ID + bookId));
        book.addRecommenced(recommenced);

        requestRepository.delete(request);

        return true;
    }

    private static BookRecommendationRequest createBookRecommendationRequest(Reader reader, Book book) {
        var recommendation = new BookRecommendationRequest();
        String token = UUID.randomUUID().toString();
        recommendation.setToken(token);
        recommendation.setRecommenced(reader);
        recommendation.setBook(book);
        book.getRecommendationRequests().add(recommendation);

        return recommendation;
    }

    private RequestParams getRequestParams(int borrowedBookId, int readerId) {
        Reader reader = readerRepository
                .findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_READER_ID + readerId));
        Optional<RecommendedBookDto> bookDto =
                borrowedBookRepository.findRecommendedBookByBorrowedBookId(borrowedBookId);

        Book book = bookDto.flatMap(this::getBook)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_BOOK_ID + borrowedBookId));

        return new RequestParams(book, reader);
    }

    private Optional<Book> getBook(RecommendedBookDto dto) {
        int bookId = dto.bookId();
        Optional<Book> book = bookRepository.findBookById(bookId);
        if (book.isPresent()) {
            return book;
        }

        return bookRepository.findById(bookId).map(b -> {
            b.setRecommendationRequests(new ArrayList<>());
            return b;
        });
    }

    private record RequestParams(Book book, Reader reader) {}
}
