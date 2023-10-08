package com.book.library.dto;

import com.book.library.book.BorrowedBook;
import java.time.LocalDate;

public record BorrowedBookDto(
        int id, LocalDate borrowedOn, String name, String author, int readerId, String firstName, String lastName)
        implements DataTransferObject {

    public BorrowedBookDto(BorrowedBook book) {
        this(
                book.getId(),
                book.getBorrowedOn(),
                book.getBook().getName(),
                book.getBook().getAuthor(),
                book.getReader().getId(),
                book.getReader().getFirstName(),
                book.getReader().getLastName());
    }

    public String readerName() {
        return "%s %s".formatted(firstName(), lastName());
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s'', borrowedTo='%s', borrowedOn='%s']"
                .formatted(id, name, author, readerName(), borrowedOn);
    }
}
