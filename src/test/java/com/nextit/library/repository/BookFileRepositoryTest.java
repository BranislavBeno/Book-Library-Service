package com.nextit.library.repository;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class BookFileRepositoryTest implements WithAssertions {

    @Autowired
    private BookFileRepository cut;

    @Test
    void testFailingRepositoryPopulating() {
        assertThatThrownBy(() -> new BookFileRepository("/src/test/repository/file.xml"))
                .isInstanceOf(BookFileNotFoundException.class);
    }

    @Test
    void testNonEmptyRepository() {
        assertThat(cut.findAll()).hasSize(6);

        Book book = cut.findAll().get(0);
        assertThat(book.getAuthor()).isEqualTo("Ernest Hemingway");

        Borrowed borrowed = book.getBorrowed();
        assertThat(borrowed).isNotNull();
        assertThat(borrowed.from()).isEqualTo(LocalDate.of(2016, 3, 25));
    }
}