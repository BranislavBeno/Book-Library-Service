package com.book.library.service;

import com.book.library.domain.FileBook;
import com.book.library.repository.BookFileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record BookService(BookFileRepository repository, int pageSize) {

    public Page<FileBook> findAll(int page) {
        return repository.findAll(getPageRequest(page));
    }

    public Page<FileBook> findAllAvailable(int page) {
        return repository.findAllAvailable(getPageRequest(page));
    }

    public Page<FileBook> findAllBorrowed(int page) {
        return repository.findAllBorrowed(getPageRequest(page));
    }

    public FileBook save(FileBook book) {
        return repository.save(book);
    }

    public boolean existsById(int id) {
        return repository.existsById(id);
    }

    public FileBook findById(int id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
