package com.book.library.dto;

import com.book.library.domain.Book;
import java.time.format.DateTimeFormatter;

public final class BookMapper {

    public AnyBookDto toAnyBookDto(Book book) {
        int id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        boolean borrowed = book.getBorrowed() != null && book.getBorrowed().from() != null;

        return new AnyBookDto(id, name, author, borrowed);
    }

    public Book toEntity(AvailableBookDto dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setName(dto.getName());
        book.setAuthor(dto.getAuthor());

        return book;
    }

    public AvailableBookDto toAvailableBookDto(Book book) {
        int id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();

        return new AvailableBookDto(id, name, author);
    }

    public BorrowedBookDto toBorrowedBookDto(Book book) {
        int id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        String borrowedTo = String.join(
                " ", book.getBorrowed().firstName(), book.getBorrowed().lastName());
        String borrowedOn = book.getBorrowed().from().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return new BorrowedBookDto(id, name, author, borrowedTo, borrowedOn);
    }
}
