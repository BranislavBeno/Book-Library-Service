package com.book.library.book;

import com.book.library.repository.BaseRepositoryTest;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

class BorrowedBookRepositoryTest extends BaseRepositoryTest<BorrowedBook> implements WithAssertions {

    @Autowired
    private BorrowedBookRepository repository;

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testFindAll() {
        List<BorrowedBook> books = repository.findAll();

        assertThat(books).hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testFindById() {
        assertEntity(r -> {
            assertThat(r.getBorrowedOn()).isEqualTo(LocalDate.of(2016, 3, 25));
            assertThat(r.getBook()).isNotNull();
            assertThat(r.getReader()).isNotNull();
        });
    }

    @Override
    protected JpaRepository<BorrowedBook, Long> getRepository() {
        return repository;
    }
}
