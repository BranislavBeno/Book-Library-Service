package com.book.library.dto;

import com.book.library.book.BorrowedBook;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class BorrowedBookDto implements DataTransferObject {

    private final int id;
    private final LocalDate borrowedOn;
    private final String name;
    private final String author;
    private final int readerId;
    private final String firstName;
    private final String lastName;
    private final List<ReaderDto> offered;

    public BorrowedBookDto(
            int id, LocalDate borrowedOn, String name, String author, int readerId, String firstName, String lastName) {
        this.id = id;
        this.borrowedOn = borrowedOn;
        this.name = name;
        this.author = author;
        this.readerId = readerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.offered = new ArrayList<>();
    }

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

    public int id() {
        return id;
    }

    public LocalDate borrowedOn() {
        return borrowedOn;
    }

    public String name() {
        return name;
    }

    public String author() {
        return author;
    }

    public int readerId() {
        return readerId;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public List<ReaderDto> offered() {
        return offered;
    }

    public void setOffered(List<ReaderDto> readers) {
        this.offered.clear();
        this.offered.addAll(readers);
    }
}
