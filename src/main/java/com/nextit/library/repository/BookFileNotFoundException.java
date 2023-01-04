package com.nextit.library.repository;

public class BookFileNotFoundException extends RuntimeException {

    public BookFileNotFoundException(String message) {
        super(message);
    }
}
