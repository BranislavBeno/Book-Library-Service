package com.book.library.dto;

import com.book.library.domain.Book;
import com.book.library.util.BookUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class BookMapperTest implements WithAssertions {

    private final BookMapper cut = new BookMapper();

    @Test
    void testMappingToBookDto() {
        Book book = BookUtils.createBook();
        AnyBookDto dto = cut.toAnyBookDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowed()).isTrue();
    }

    @Test
    void testMappingToAvailableDto() {
        Book book = BookUtils.createBook();
        AvailableBookDto dto = cut.toAvailableBookDto(book);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Hamlet");
        assertThat(dto.getAuthor()).isEqualTo("William Shakespeare");
    }

    @Test
    void testMappingFromAvailableDto() {
        AvailableBookDto dto = BookUtils.createAvailableDto();
        Book book = cut.toEntity(dto);

        assertThat(book.getId()).isEqualTo(1);
        assertThat(book.getName()).isEqualTo("Hamlet");
        assertThat(book.getAuthor()).isEqualTo("William Shakespeare");
    }

    @Test
    void testMappingToBorrowedDto() {
        Book book = BookUtils.createBook();
        BorrowedBookDto dto = cut.toBorrowedBookDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowedTo()).isEqualTo("Peter Pavol");
        assertThat(dto.borrowedOn()).isEqualTo("04/01/2023");
    }
}