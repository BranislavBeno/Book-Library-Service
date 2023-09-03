package com.book.library.book;

import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public final class BorrowedDto {

    private int bookId;
    private int readerId;

    @PastOrPresent(message = "Borrow date can't be later than today.")
    private LocalDate from;

    public BorrowedDto(int bookId, int readerId, LocalDate from) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.from = from;
    }

    public BorrowedDto() {
        this(0, 0, null);
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }
}
