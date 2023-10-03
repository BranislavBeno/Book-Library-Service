package com.book.library.book;

import com.book.library.reader.ReaderRepository;
import com.book.library.repository.BaseTestRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookDataServiceTest extends BaseTestRepository implements WithAssertions {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    private final BookService service = new BookService(bookRepository, borrowedBookRepository, readerRepository, 5);

    @Test
    void testOfferedCount() {
        assertThat(service).isNotNull();
    }
}
