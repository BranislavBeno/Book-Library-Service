package com.book.library.dto;

import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public final class BorrowedDto {

    private int bookId;
    private String firstName;
    private String lastName;

    @PastOrPresent(message = "Borrow date can't be later than today.")
    private LocalDate from;

    public BorrowedDto(int bookId, String firstName, String lastName, LocalDate from) {
        this.bookId = bookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.from = from;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }
}
