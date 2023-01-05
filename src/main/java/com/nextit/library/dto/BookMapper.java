package com.nextit.library.dto;

import com.nextit.library.domain.Book;

import java.time.format.DateTimeFormatter;

public final class BookMapper {

    public BookDto toBookDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        boolean borrowed = book.getBorrowed() != null && book.getBorrowed().from() != null;

        return new BookDto(id, name, author, borrowed);
    }

    public BookAvailableDto toAvailableDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();

        return new BookAvailableDto(id, name, author);
    }

    public BookBorrowedDto toBorrowedDto(Book book) {
        long id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        String borrowedTo = String.join(" ", book.getBorrowed().firstName(), book.getBorrowed().lastName());
        String borrowedOn = book.getBorrowed().from().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new BookBorrowedDto(id, name, author, borrowedTo, borrowedOn);
    }
}
