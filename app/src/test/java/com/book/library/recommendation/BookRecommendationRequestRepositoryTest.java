package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.book.BookRepository;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import com.book.library.repository.AbstractTestRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class BookRecommendationRequestRepositoryTest extends AbstractTestRepository<BookRecommendationRequest>
        implements WithAssertions {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookRecommendationRequestRepository requestRepository;

    @ParameterizedTest
    @CsvSource({"1,2,true", "2,1,false", "5,2,false", "1,10,false"})
    void testRequestExists(int borrowedBookId, int readerId, boolean result) {
        RequestParams params = getRequestParams(borrowedBookId, readerId);
        assertThat(requestRepository.existsByBookAndRecommenced(params.book, params.reader))
                .isEqualTo(result);
    }

    @Override
    protected JpaRepository<BookRecommendationRequest, Integer> getRepository() {
        return requestRepository;
    }

    private RequestParams getRequestParams(int borrowedBookId, int readerId) {
        Reader reader = readerRepository.findById(readerId).orElse(null);
        Book book = borrowedBookRepository
                .findRecommendedBookByBorrowedBookId(borrowedBookId)
                .flatMap(b -> bookRepository.findById(b.bookId()))
                .orElse(null);

        return new RequestParams(book, reader);
    }

    private record RequestParams(Book book, Reader reader) {}
}
