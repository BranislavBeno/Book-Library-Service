package com.book.library.book;

class BookNotFoundException extends RuntimeException {

    BookNotFoundException(String message) {
        super(message);
    }
}
