package com.book.library.book;

import com.book.library.repository.BaseRepositoryTest;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class BorrowedBookRepositoryTest extends BaseRepositoryTest implements WithAssertions {

    @Autowired
    private BorrowedBookRepository repository;

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testFindAll() {
        List<BorrowedBook> books = repository.findAll();

        assertThat(books).hasSize(2);
    }
}
