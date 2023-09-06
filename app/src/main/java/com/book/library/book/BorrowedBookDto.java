package com.book.library.book;

import java.time.LocalDate;

public record BorrowedBookDto(
        int id, LocalDate borrowedOn, String name, String author, String firstName, String lastName)
        implements BookDto {

    public BorrowedBookDto(BorrowedBook book) {
        this(
                book.getId(),
                book.getBorrowedOn(),
                book.getBook().getName(),
                book.getBook().getAuthor(),
                book.getReader().getFirstName(),
                book.getReader().getLastName());
    }

    public String readerName() {
        return "%s %s".formatted(firstName(), lastName());
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s'', borrowedTo='%s %s', borrowedOn='%s']"
                .formatted(id, name, author, firstName, lastName, borrowedOn);
    }
}
