package com.book.library.book;

import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import com.book.library.repository.BaseRepositoryTest;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

class BorrowedBookRepositoryTest extends BaseRepositoryTest<BorrowedBook> implements WithAssertions {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BorrowedBookRepository repository;

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testFindAll() {
        Page<BorrowedBook> books = repository.findAll(getPageRequest());
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

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testDeleteById() {
        repository.deleteById(1L);
        Page<BorrowedBook> borrowedBooks = repository.findAll(getPageRequest());

        assertThat(borrowedBooks).hasSize(1);
    }

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testAddBorrowedBook() {
        var borrowedBook = createBorrowedBook();
        borrowedBook.ifPresentOrElse(
                b -> {
                    repository.save(b);
                    Page<BorrowedBook> bookList = repository.findAll(getPageRequest());
                    assertThat(bookList).hasSize(3);
                },
                () -> Assertions.fail("Borrowed book creation failed"));
    }

    @Test
    @Sql(scripts = "/sql/init_borrowed_book.sql")
    void testAvailableBooks() {
        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> Objects.isNull(b.getBorrowed()))
                .toList();
        Page<Book> availableBooks = provideBookPage(getPageRequest(), books);

        assertThat(availableBooks).hasSize(1);
    }

    @Override
    protected JpaRepository<BorrowedBook, Long> getRepository() {
        return repository;
    }

    private Optional<BorrowedBook> createBorrowedBook() {
        Optional<Reader> reader = readerRepository.findById(1L);
        Optional<Book> book = bookRepository.findById(1L);

        return reader.flatMap(r -> book.map(b -> {
            BorrowedBook bb = new BorrowedBook();
            bb.setBook(b);
            bb.setReader(r);
            return bb;
        }));
    }

    private PageImpl<Book> provideBookPage(Pageable pageable, List<Book> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Book> list = books.size() < startItem
                ? Collections.emptyList()
                : books.subList(startItem, Math.min(startItem + pageSize, books.size()));

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
    }
}
