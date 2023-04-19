package com.book.library.dto;

public record BorrowedBookDto(int id, String name, String author, String borrowedTo, String borrowedOn)
        implements BookDto {}
