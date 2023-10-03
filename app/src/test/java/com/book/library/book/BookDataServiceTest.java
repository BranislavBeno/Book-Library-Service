package com.book.library.book;

import com.book.library.dto.AnyBookDto;
import com.book.library.dto.ReaderDto;
import com.book.library.reader.ReaderRepository;
import com.book.library.repository.BaseTestRepository;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class BookDataServiceTest extends BaseTestRepository implements WithAssertions {

    @Autowired
    private BookService service;

    private List<AnyBookDto> books;

    @BeforeEach
    void setUp() {
        assertThat(service).isNotNull();
        books = service.findBooks(0).getContent();
    }

    @Test
    void testOfferedCount() {
        List<ReaderDto> offered = books.get(0).offered();
        assertThat(offered).hasSize(6);

        offered = books.get(3).offered();
        assertThat(offered).hasSize(7);
    }

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        @Bean
        public BookService bookService(
                @Autowired BookRepository bookRepository,
                @Autowired BorrowedBookRepository borrowedBookRepository,
                @Autowired ReaderRepository readerRepository) {
            return new BookService(bookRepository, borrowedBookRepository, readerRepository, 5);
        }
    }
}
