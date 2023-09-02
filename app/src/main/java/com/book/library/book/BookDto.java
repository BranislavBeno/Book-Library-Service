package com.book.library.book;

sealed interface BookDto permits AnyBookDto, AvailableBookDto, BorrowedBookDto {}
