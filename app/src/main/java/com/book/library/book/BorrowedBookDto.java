package com.book.library.book;

import java.time.LocalDate;

public record BorrowedBookDto(
        long id, LocalDate borrowedOn, String name, String author, String firstName, String lastName) {}
