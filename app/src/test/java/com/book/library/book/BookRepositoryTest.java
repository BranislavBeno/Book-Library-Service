package com.book.library.book;

import com.book.library.repository.AbstractTestRepository;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({"1,Starec a more,Ernest Hemingway,false", "4,Hájniková žena, Pavol Országh Hviezdoslav,true"})
    void testFindById(int bookId, String name, String author, boolean isNull) {
        assertEntity(bookId, b -> {
            assertThat(b.getName()).isEqualTo(name);
            assertThat(b.getAuthor()).isEqualTo(author);
            assertThat(b.getBorrowed() == null).isEqualTo(isNull);
        });
    }

    @Test
    void testFindBookById() {
        Optional<Book> book = repository.findBookById(1);
        book.ifPresentOrElse(
                b -> {
                    assertThat(b.bookInfo()).isEqualTo("Starec a more written by Ernest Hemingway");
                    assertThat(b.getRecommendationRequests()).hasSize(1);
                },
                () -> fail("Book not found."));
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
