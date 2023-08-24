package com.book.library.book;

import com.book.library.reader.ReaderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record BookService(
        BookRepository bookRepository,
        BorrowedBookRepository borrowedBookRepository,
        ReaderRepository readerRepository,
        int pageSize) {

    public Page<Book> findAll(int page) {
        return bookRepository.findAll(getPageRequest(page));
    }

    public Page<AvailableBookDto> findAllAvailable(int page) {
        return bookRepository.findAllAvailableBooks(getPageRequest(page));
    }

    public Page<BorrowedBookDto> findAllBorrowed(int page) {
        return borrowedBookRepository.findAllBorrowedBooks(getPageRequest(page));
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
