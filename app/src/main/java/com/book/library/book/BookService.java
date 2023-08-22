package com.book.library.book;

import com.book.library.reader.ReaderRepository;

public record BookService(
        BookRepository bookRepository,
        BorrowedBookRepository borrowedBookRepository,
        ReaderRepository readerRepository,
        int pageSize) {}
