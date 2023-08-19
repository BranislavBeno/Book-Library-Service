package com.book.library.book;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class BookRepositoryTest extends BaseRepositoryTest implements WithAssertions {

    @Autowired
    private BookRepository repository;

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testFindAll() {
        List<Book> books = repository.findAll();

        assertThat(books).hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testFindById() {
        assertBook(r -> {
            assertThat(r.getName()).isEqualTo("The Old Man and the Sea");
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
        });
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testDeleteById() {
        repository.deleteById(1L);
        List<Book> books = repository.findAll();

        assertThat(books).hasSize(1);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testAddBook() {
        Book book = createBook();
        repository.save(book);
        List<Book> books = repository.findAll();

        assertThat(books).hasSize(3);
    }

    @Test
    @Sql(scripts = "/sql/init_book.sql")
    void testUpdateReader() {
        assertBook(r -> {
            assertThat(r.getAuthor()).isEqualTo("Ernest Hemingway");
            r.setAuthor("William Shakespeare");
            repository.save(r);
        });

        assertBook(r -> assertThat(r.getAuthor()).isEqualTo("William Shakespeare"));
    }

    private void assertBook(Consumer<Book> consumer) {
        Optional<Book> reader = repository.findById(1L);
        reader.ifPresentOrElse(consumer, () -> fail("Book not found"));
    }

    @NotNull
    private static Book createBook() {
        var book = new Book();
        book.setName("Hamlet");
        book.setAuthor("William Shakespeare");

        return book;
    }
}
