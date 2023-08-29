package com.book.library.book;

import java.time.LocalDate;

public record BorrowedBookDto(
        long id, LocalDate borrowedOn, String name, String author, String firstName, String lastName) {

    public BorrowedBookDto(BorrowedBook book) {
        this(
                book.getId(),
                book.getBorrowedOn(),
                book.getBook().getName(),
                book.getBook().getAuthor(),
                book.getReader().getFirstName(),
                book.getReader().getLastName());
    }
}
