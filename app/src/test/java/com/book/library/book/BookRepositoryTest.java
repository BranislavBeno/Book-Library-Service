package com.book.library.book;

import com.book.library.repository.AbstractTestRepository;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class BookRepositoryTest extends AbstractTestRepository<Book> implements WithAssertions {

    @Autowired
    private BookRepository repository;

    @Test
    void testFindAll() {
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(5);
    }

    @Test
    void testFindById() {
        assertEntity(1, r -> {
            assertThat(r.getName()).isEqualTo("Starec a more");
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
        });
    }

    @Test
    void testDeleteById() {
        repository.deleteById(4);
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(5);
    }

    @Test
    void testAddBook() {
        Book book = createBook();
        repository.save(book);
        Page<Book> books = repository.findAll(getPageRequest());

        assertThat(books).hasSize(5);
    }

    @Test
    void testUpdateReader() {
        assertEntity(1, r -> {
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
            r.setAuthor("William Shakespeare");
            repository.save(r);
        });

        assertEntity(1, r -> assertThat(r.getAuthor()).isEqualTo("William Shakespeare"));
    }

    @NotNull
    private static Book createBook() {
        var book = new Book();
        book.setName("Macbeth");
        book.setAuthor("William Shakespeare");

        return book;
    }

    @Override
    protected JpaRepository<Book, Integer> getRepository() {
        return repository;
    }
}
