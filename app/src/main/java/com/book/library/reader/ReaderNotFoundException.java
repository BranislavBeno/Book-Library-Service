package com.book.library.reader;

class ReaderNotFoundException extends RuntimeException {

    ReaderNotFoundException(String message) {
        super(message);
    }
}
