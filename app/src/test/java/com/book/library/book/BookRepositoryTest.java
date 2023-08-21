package com.book.library.book;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

class BookRepositoryTest extends BaseRepositoryTest<Book> implements WithAssertions {

    @Autowired
    private BookRepository repository;

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testFindAll() {
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testFindById() {
        assertEntity(r -> {
            assertThat(r.getName()).isEqualTo("The Old Man and the Sea");
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
        });
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testDeleteById() {
        repository.deleteById(1L);
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(1);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testAddBook() {
        Book book = createBook();
        repository.save(book);
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(3);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testUpdateReader() {
        assertEntity(r -> {
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
            r.setAuthor("William Shakespeare");
            repository.save(r);
        });

        assertEntity(r -> assertThat(r.getAuthor()).isEqualTo("William Shakespeare"));
    }

    @NotNull
    private static Book createBook() {
        var book = new Book();
        book.setName("Hamlet");
        book.setAuthor("William Shakespeare");

        return book;
    }

    @Override
    protected JpaRepository<Book, Long> getRepository() {
        return repository;
    }
}
