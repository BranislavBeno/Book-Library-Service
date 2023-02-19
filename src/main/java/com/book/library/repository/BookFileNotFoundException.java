package com.book.library.repository;

public final class BookFileNotFoundException extends RuntimeException {

    public BookFileNotFoundException(String message) {
        super(message);
    }
}
