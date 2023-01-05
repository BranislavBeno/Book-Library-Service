package com.nextit.library.dto;

import com.nextit.library.domain.Book;

import java.time.format.DateTimeFormatter;

public final class BookMapper {

    public AnyBookDto toAnyDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        boolean borrowed = book.getBorrowed() != null && book.getBorrowed().from() != null;

        return new AnyBookDto(id, name, author, borrowed);
    }

    public AvailableBookDto toAvailableDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();

        return new AvailableBookDto(id, name, author);
    }

    public BorrowedBookDto toBorrowedDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        String borrowedTo = String.join(" ", book.getBorrowed().firstName(), book.getBorrowed().lastName());
        String borrowedOn = book.getBorrowed().from().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new BorrowedBookDto(id, name, author, borrowedTo, borrowedOn);
    }
}
