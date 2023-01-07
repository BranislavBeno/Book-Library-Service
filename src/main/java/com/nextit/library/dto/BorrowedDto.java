package com.nextit.library.dto;

import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record BorrowedDto(int bookId, String firstName, String lastName,
                          @PastOrPresent(message = "Borrow date can't be later than today.") LocalDate from) {
}
