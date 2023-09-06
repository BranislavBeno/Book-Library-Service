package com.book.library.filedto;

public sealed interface BookDto permits AnyBookDto, AvailableBookDto, BorrowedBookDto {}
