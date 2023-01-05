package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.nio.file.Path;
import java.time.LocalDate;

@SpringBootTest
class BookFileRepositoryTest implements WithAssertions {

    @TempDir
    private Path path;
    @Autowired
    private BookFileRepository cut;

    @Test
    void testFailingRepositoryImport() {
        assertThatThrownBy(() -> new BookFileRepository("src/test/resources/Dummy.xml"))
                .isInstanceOf(BookFileNotFoundException.class);
    }

    @Test
    void testEmptyBookPage() {
        Page<Book> books = cut.findAll(PageRequest.of(1, 10));
        assertThat(books).isEmpty();
    }

    @Test
    void testAllBooks() {
        Page<Book> books = cut.findAll(PageRequest.of(0, 5));
        assertThat(books).hasSize(5);

        Book book = books.getContent().get(0);
        assertThat(book.getAuthor()).isEqualTo("Ernest Hemingway");

        Borrowed borrowed = book.getBorrowed();
        assertThat(borrowed).isNotNull();
        assertThat(borrowed.from()).isEqualTo(LocalDate.of(2016, 3, 25));
    }

    @Test
    void testAvailableBooks() {
        assertThat(cut.findAllAvailable()).hasSize(2);
    }

    @Test
    void testBorrowedBooks() {
        assertThat(cut.findAllBorrowed()).hasSize(4);
    }

    @Test
    void testRepositoryExport() {
        String filePath = path.resolve("temp.xml").toString();
        cut.exportToFile(filePath);

        PageRequest request = PageRequest.of(1, 5);
        int size = cut.findAll(request).getContent().size();
        BookFileRepository repository = new BookFileRepository(filePath);
        Page<Book> items = repository.findAll(request);

        assertThat(items).hasSize(size);
    }
}