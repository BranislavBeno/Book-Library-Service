package com.nextit.library.util;

import com.nextit.library.domain.Book;
import com.nextit.library.domain.Borrowed;
import com.nextit.library.dto.AvailableBookDto;

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
}
