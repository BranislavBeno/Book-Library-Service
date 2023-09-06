package com.book.library.filedto;

public record BorrowedBookDto(int id, String name, String author, String borrowedTo, String borrowedOn)
        implements BookDto {}
