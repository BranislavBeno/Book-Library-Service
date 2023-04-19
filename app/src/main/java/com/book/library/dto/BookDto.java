package com.book.library.dto;

public sealed interface BookDto permits AnyBookDto, AvailableBookDto, BorrowedBookDto {}
