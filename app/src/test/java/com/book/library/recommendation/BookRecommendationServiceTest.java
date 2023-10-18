package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.book.BookRepository;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.dto.RecommendedBookDto;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookRecommendationServiceTest implements WithAssertions {

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookRecommendationRequestRepository requestRepository;

    @Mock
    private SqsTemplate sqsTemplate;

    @Mock
    private Reader reader;

    @Mock
    private Book book;

    @Mock
    private RecommendedBookDto recommendedBookDto;

    @Mock
    private SendResult<BookRecommendationNotification> result;

    private BookRecommendationService service;

    @BeforeEach
    void setUp() {
        service = new BookRecommendationService(
                borrowedBookRepository, bookRepository, readerRepository, requestRepository, sqsTemplate, "");
    }

    @Test
    void testReaderNotFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommendBookTo(bookId, readerId)).hasNoCause();

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verifyNoInteractions(borrowedBookRepository);
    }

    @Test
    void testBorrowedBookNotFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommendBookTo(bookId, readerId)).hasNoCause();

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verifyNoInteractions(bookRepository);
    }

    @Test
    void testBookWithRequestNotFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.of(recommendedBookDto));
        Mockito.when(bookRepository.findBookById(recommendedBookDto.bookId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommendBookTo(bookId, readerId)).hasNoCause();

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verify(bookRepository).findBookById(recommendedBookDto.bookId());
        Mockito.verify(bookRepository).findById(recommendedBookDto.bookId());
        Mockito.verifyNoInteractions(requestRepository);
    }

    @Test
    void testBookNotFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.of(recommendedBookDto));
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recommendBookTo(bookId, readerId)).hasNoCause();

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verify(bookRepository).findBookById(recommendedBookDto.bookId());
        Mockito.verify(bookRepository).findById(recommendedBookDto.bookId());
        Mockito.verifyNoInteractions(requestRepository);
    }

    @Test
    void testAlreadyRecommendedBookWithRequestFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.of(recommendedBookDto));
        Mockito.when(bookRepository.findBookById(recommendedBookDto.bookId())).thenReturn(Optional.of(book));
        Mockito.when(requestRepository.existsByBookAndRecommenced(book, reader)).thenReturn(true);

        service.recommendBookTo(bookId, readerId);

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verify(bookRepository).findBookById(recommendedBookDto.bookId());
        Mockito.verify(bookRepository, Mockito.never()).findById(recommendedBookDto.bookId());
        Mockito.verify(requestRepository).existsByBookAndRecommenced(book, reader);
        Mockito.verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void testAlreadyRecommendedBookFound() {
        int readerId = 3;
        int bookId = 1;
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.of(recommendedBookDto));
        Mockito.when(bookRepository.findById(recommendedBookDto.bookId())).thenReturn(Optional.of(book));
        Mockito.when(requestRepository.existsByBookAndRecommenced(book, reader)).thenReturn(true);

        service.recommendBookTo(bookId, readerId);

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verify(bookRepository).findBookById(recommendedBookDto.bookId());
        Mockito.verify(bookRepository).findById(recommendedBookDto.bookId());
        Mockito.verify(requestRepository).existsByBookAndRecommenced(book, reader);
        Mockito.verifyNoMoreInteractions(requestRepository);
    }

    @Test
    void testNotYetRecommendedBookFound() {
        int readerId = 3;
        int bookId = 1;
        var request = createRequest(reader, book);
        var notification = new BookRecommendationNotification(request);
        String queueName = service.bookRecommendationQueueName();

        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.findRecommendedBookByBorrowedBookId(bookId))
                .thenReturn(Optional.of(recommendedBookDto));
        Mockito.when(bookRepository.findById(recommendedBookDto.bookId())).thenReturn(Optional.of(book));
        Mockito.when(requestRepository.existsByBookAndRecommenced(book, reader)).thenReturn(false);
        Mockito.when(requestRepository.save(Mockito.any(BookRecommendationRequest.class)))
                .thenReturn(request);
        Mockito.lenient().when(sqsTemplate.send(queueName, notification)).thenReturn(result);

        service.recommendBookTo(bookId, readerId);

        Mockito.verify(readerRepository).findById(readerId);
        Mockito.verify(borrowedBookRepository).findRecommendedBookByBorrowedBookId(bookId);
        Mockito.verify(bookRepository).findBookById(recommendedBookDto.bookId());
        Mockito.verify(bookRepository).findById(recommendedBookDto.bookId());
        Mockito.verify(requestRepository).existsByBookAndRecommenced(book, reader);
        Mockito.verify(requestRepository).save(Mockito.any(BookRecommendationRequest.class));
    }

    private static BookRecommendationRequest createRequest(Reader reader, Book book) {
        var request = new BookRecommendationRequest();
        String token = UUID.randomUUID().toString();
        request.setToken(token);
        request.setRecommenced(reader);
        request.setBook(book);
        book.getRecommendationRequests().add(request);

        return request;
    }
}
