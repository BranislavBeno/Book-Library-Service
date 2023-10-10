package com.book.library.book;

import com.book.library.dto.AvailableBookDto;
import com.book.library.dto.BorrowedBookDto;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import com.book.library.repository.AbstractTestRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class BorrowedBookRepositoryTest extends AbstractTestRepository<BorrowedBook> implements WithAssertions {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BorrowedBookRepository repository;

    @Test
    void testFindAll() {
        Page<BorrowedBookDto> borrowedBooks = repository.findAllBorrowedBooks(getPageRequest());

        assertThat(borrowedBooks).hasSize(4);
    }

    @Test
    void testFindById() {
        assertEntity(1, r -> {
            assertThat(r.getBorrowedOn()).isEqualTo(LocalDate.of(2016, 3, 25));
            assertThat(r.getBook()).isNotNull();
            assertThat(r.getReader()).isNotNull();
        });
    }

    @Test
    void testFindAnyBookByBorrowedBookId() {
        repository
                .findRecommendedBookByBorrowedBookId(4)
                .ifPresentOrElse(
                        b -> {
                            assertThat(b.book()).isEqualTo("Hamlet from William Shakespeare");
                            assertThat(b.readerName()).isEqualTo("Jozef Štvrtý");
                            assertThat(b.readerEmail()).isEqualTo("jozef@example.com");
                        },
                        () -> Assertions.fail("Recommended book fetching failed"));
    }

    @Test
    void testDeleteById() {
        repository.deleteById(1);
        Page<BorrowedBook> borrowedBooks = repository.findAll(getPageRequest());

        assertThat(borrowedBooks).hasSize(3);
    }

    @Test
    void testAddBorrowedBook() {
        var borrowedBook = createBorrowedBook();
        borrowedBook.ifPresentOrElse(
                b -> {
                    repository.save(b);
                    Page<BorrowedBook> bookList = repository.findAll(getPageRequest());
                    assertThat(bookList).hasSize(5);
                },
                () -> Assertions.fail("Borrowed book creation failed"));
    }

    @Test
    void testAvailableBooks() {
        Page<AvailableBookDto> availableBooks = bookRepository.findAllAvailableBooks(getPageRequest());

        assertThat(availableBooks).hasSize(2);
    }

    @Override
    protected JpaRepository<BorrowedBook, Integer> getRepository() {
        return repository;
    }

    private Optional<BorrowedBook> createBorrowedBook() {
        Optional<Reader> reader = readerRepository.findById(1);
        Optional<Book> book = bookRepository.findById(1);

        return reader.flatMap(r -> book.map(b -> {
            BorrowedBook bb = new BorrowedBook();
            bb.setBook(b);
            bb.setReader(r);
            return bb;
        }));
    }
}
