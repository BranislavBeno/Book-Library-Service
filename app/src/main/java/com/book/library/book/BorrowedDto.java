package com.book.library.book;

import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record BorrowedDto(
        long bookId, long readerId, @PastOrPresent(message = "Borrow date can't be later than today.") LocalDate from) {

    public BorrowedDto(long bookId) {
        this(bookId, 0, null);
    }
}
