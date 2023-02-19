package com.book.library.util;

import com.book.library.domain.Book;
import com.book.library.domain.Borrowed;
import com.book.library.dto.AvailableBookDto;
import com.book.library.dto.BorrowedDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

public final class BookUtils {

    private BookUtils() {
    }

    public static Book createBook() {
        Book book = new Book();
        book.setId(1);
        book.setName("Hamlet");
        book.setAuthor("William Shakespeare");
        Borrowed borrowed = new Borrowed("Peter", "Pavol", LocalDate.of(2023, 1, 4));
        book.setBorrowed(borrowed);

        return book;
    }

    public static AvailableBookDto createAvailableDto() {
        return new AvailableBookDto(1, "Hamlet", "William Shakespeare");
    }

    public static String createNonValidBorrowRequest() {
        return createNonValidRequest(new BorrowedDto(4, "John", "Doe", getTomorrowsDate()));
    }

    public static LocalDate getTomorrowsDate() {
        return LocalDate.now().plusDays(1);
    }

    private static String createNonValidRequest(BorrowedDto dto) {
        try {
            JsonMapper jsonMapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();
            return jsonMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
