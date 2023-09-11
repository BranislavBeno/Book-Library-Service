package com.book.library.reader;

abstract class AbstractReaderController {

    private final ReaderService service;

    AbstractReaderController(ReaderService service) {
        this.service = service;
    }

    ReaderService getService() {
        return service;
    }
}
