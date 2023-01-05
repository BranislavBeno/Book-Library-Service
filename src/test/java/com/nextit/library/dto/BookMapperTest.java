package com.nextit.library.dto;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class BookMapperTest implements WithAssertions {

    private final BookMapper cut = new BookMapper();

    @Test
    void testMappingToBookDto() {
        Book book = createBook();
        BookDto dto = cut.toBookDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowed()).isTrue();
    }

    @Test
    void testMappingToAvailableDto() {
        Book book = createBook();
        BookAvailableDto dto = cut.toAvailableDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
    }

    @Test
    void testMappingToBorrowedDto() {
        Book book = createBook();
        BookBorrowedDto dto = cut.toBorrowedDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowedTo()).isEqualTo("Peter Pavol");
        assertThat(dto.borrowedOn()).isEqualTo("04/01/2023");
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1);
        book.setName("Hamlet");
        book.setAuthor("William Shakespeare");
        Borrowed borrowed = new Borrowed("Peter", "Pavol", LocalDate.of(2023, 1, 4));
        book.setBorrowed(borrowed);

        return book;
    }
}