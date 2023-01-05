package com.nextit.library.repository;

public final class BookFileNotFoundException extends RuntimeException {

    public BookFileNotFoundException(String message) {
        super(message);
    }
}
