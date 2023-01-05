package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookServiceTest implements WithAssertions {

    @Mock
    private BookRepository repository;
    @Mock
    private Page<Book> page;
    private BookService cut;

    @BeforeEach
    void setUp() {
        cut = new BookService(repository, 5);
    }

    @Test
    void testFindAll() {
        Mockito.when(repository.findAll(any(PageRequest.class))).thenReturn(page);
        cut.findAll(1);
        Mockito.verify(repository).findAll(any(PageRequest.class));
    }

    @Test
    void testFindAllAvailable() {
        Mockito.when(repository.findAllAvailable()).thenReturn(List.of(new Book(), new Book()));
        cut.findAllAvailable();
        Mockito.verify(repository).findAllAvailable();
    }

    @Test
    void testFindAllBorrowed() {
        Mockito.when(repository.findAllBorrowed()).thenReturn(Collections.emptyList());
        cut.findAllBorrowed();
        Mockito.verify(repository).findAllBorrowed();
    }
}