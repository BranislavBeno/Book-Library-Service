package com.nextit.library.repository;

import com.nextit.library.domain.Book;

import java.util.ArrayList;
import java.util.List;

public class BookFileRepository {

    private List<Book> books;

    public BookFileRepository(String path) {
        initializeRepository(path);
    }

    private void initializeRepository(String path) {
        books = new ArrayList<>();
        Book book = new Book();
        books.add(book);
    }

    public List<Book> findAll() {
        return books;
    }
}
