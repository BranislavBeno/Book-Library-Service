package com.book.library.book;

import com.book.library.reader.ReaderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record BookService(
        BookRepository bookRepository,
        BorrowedBookRepository borrowedBookRepository,
        ReaderRepository readerRepository,
        int pageSize) {

    public Page<Book> findAll(int page) {
        return bookRepository.findAll(getPageRequest(page));
    }

    public Page<Book> findAllAvailable(int page) {
        List<Book> books = bookRepository.findAll().stream()
                .filter(b -> Objects.isNull(b.getBorrowed()))
                .toList();
        return provideBookPage(getPageRequest(page), books);
    }

    public Page<Book> findAllBorrowed(int page) {
        List<Book> borrowedBooks = borrowedBookRepository.findAll(getPageRequest(page))
                .map(BorrowedBook::getBook)
                .toList();
        return provideBookPage(getPageRequest(page), borrowedBooks);
    }

    private PageImpl<Book> provideBookPage(Pageable pageable, List<Book> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Book> list = books.size() < startItem
                ? Collections.emptyList()
                : books.subList(startItem, Math.min(startItem + pageSize, books.size()));

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
