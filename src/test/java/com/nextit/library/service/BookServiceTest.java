package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookServiceTest implements WithAssertions {

    @Mock
    private BookRepository repository;
    @InjectMocks
    private BookService cut;

    @Test
    void testFindAll() {
        Mockito.when(repository.findAll()).thenReturn(List.of(new Book()));
        List<Book> books = cut.findAll();

        Mockito.verify(repository).findAll();
        assertThat(books).hasSize(1);
    }

    @Test
    void testFindAllAvailable() {
        Mockito.when(repository.findAllAvailable()).thenReturn(List.of(new Book(), new Book()));
        List<Book> books = cut.findAllAvailable();

        Mockito.verify(repository).findAllAvailable();
        assertThat(books).hasSize(2);
    }

    @Test
    void testFindAllBorrowed() {
        Mockito.when(repository.findAllBorrowed()).thenReturn(Collections.emptyList());
        List<Book> books = cut.findAllBorrowed();

        Mockito.verify(repository).findAllBorrowed();
        assertThat(books).isEmpty();
    }
}