package com.book.library.repository;

import com.book.library.domain.Book;
import com.book.library.domain.Borrowed;
import com.book.library.util.BookUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

class BookFileRepositoryTest implements WithAssertions {

    private static BookFileRepository cut;

    @BeforeAll
    static void setUp() {
        cut = new BookFileRepository("src/test/resources/Library.xml");
    }

    @Test
    void testFailingRepositoryImport() {
        assertThatThrownBy(() -> new BookFileRepository("src/test/resources/Dummy.xml"))
                .isInstanceOf(BookFileNotFoundException.class);
    }

    @Test
    void testFindEmptyBookPage() {
        Page<Book> books = cut.findAll(PageRequest.of(1, 10));
        assertThat(books).isEmpty();
    }

    @Test
    void testFindAllBooks() {
        Page<Book> page = cut.findAll(getRequest());
        assertThat(page).hasSize(5);

        Book book = page.getContent().get(0);
        assertThat(book.getAuthor()).isEqualTo("Ernest Hemingway");

        Borrowed borrowed = book.getBorrowed();
        assertThat(borrowed).isNotNull();
        assertThat(borrowed.from()).isEqualTo(LocalDate.of(2016, 3, 25));
    }

    @Test
    void testFindAvailableBooks() {
        Page<Book> page = cut.findAllAvailable(getRequest());
        assertThat(page).hasSize(2);
    }

    @Test
    void testFindBorrowedBooks() {
        Page<Book> page = cut.findAllBorrowed(getRequest());
        assertThat(page).hasSize(4);
    }

    @Test
    void testCreateNewBook() {
        PageRequest request = PageRequest.of(0, 10);
        int previousSize = cut.findAll(request).getContent().size();

        Book entity = new Book();
        entity.setName("King Rat");
        entity.setAuthor("James Clavell");

        assertThat(entity.getId()).isZero();
        cut.save(entity);
        assertThat(entity.getId()).isNotZero();

        int currentSize = cut.findAll(request).getContent().size();
        assertThat(currentSize).isGreaterThan(previousSize);
    }

    @Test
    void testChangeExistingBook() {
        PageRequest request = PageRequest.of(0, 10);
        int previousSize = cut.findAll(request).getContent().size();

        Book entity = new Book();
        entity.setId(5);
        entity.setName("Macbeth");
        Borrowed borrowed = new Borrowed("Maria", "Tudor", LocalDate.now());
        entity.setBorrowed(borrowed);

        Book book = cut.save(entity);
        assertThat(entity.getId()).isEqualTo(book.getId());

        int currentSize = cut.findAll(request).getContent().size();
        assertThat(currentSize).isEqualTo(previousSize);
    }

    @ParameterizedTest
    @CsvSource({"1,true", "10,false"})
    void testBookExists(int id, boolean found) {
        assertThat(cut.existsById(id)).isEqualTo(found);
    }

    @Test
    void testBookFound() {
        Book book = BookUtils.createBook();
        cut.findById(5).ifPresent(b -> assertThat(b.getAuthor()).isEqualTo(book.getAuthor()));
    }

    @Test
    void testBookNotFound() {
        assertThat(cut.findById(50)).isEqualTo(Optional.empty());
    }

    private static PageRequest getRequest() {
        return PageRequest.of(0, 5);
    }

    @Test
    void testDeleteBook() {
        PageRequest request = PageRequest.of(0, 10);
        int previousSize = cut.findAll(request).getContent().size();
        cut.deleteById(3);

        int currentSize = cut.findAll(request).getContent().size();
        assertThat(currentSize).isLessThan(previousSize);
    }
}