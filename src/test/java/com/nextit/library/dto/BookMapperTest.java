package com.nextit.library.dto;

import com.nextit.library.domain.Book;
import com.nextit.library.util.BookUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class BookMapperTest implements WithAssertions {

    private final BookMapper cut = new BookMapper();

    @Test
    void testMappingToBookDto() {
        Book book = BookUtils.createBook();
        AnyBookDto dto = cut.toAnyDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowed()).isTrue();
    }

    @Test
    void testMappingToAvailableDto() {
        Book book = BookUtils.createBook();
        AvailableBookDto dto = cut.toAvailableDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
    }

    @Test
    void testMappingToBorrowedDto() {
        Book book = BookUtils.createBook();
        BorrowedBookDto dto = cut.toBorrowedDto(book);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Hamlet");
        assertThat(dto.author()).isEqualTo("William Shakespeare");
        assertThat(dto.borrowedTo()).isEqualTo("Peter Pavol");
        assertThat(dto.borrowedOn()).isEqualTo("04/01/2023");
    }
}