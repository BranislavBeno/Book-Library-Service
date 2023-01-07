package com.nextit.library.service;

import com.nextit.library.domain.Book;
import com.nextit.library.repository.BookRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookServiceTest implements WithAssertions {

    @Mock
    private BookRepository repository;
    @Mock
    private Page<Book> page;
    @Mock
    private Book book;
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
        Mockito.when(repository.findAllAvailable(any(PageRequest.class))).thenReturn(page);
        cut.findAllAvailable(0);
        Mockito.verify(repository).findAllAvailable(any(PageRequest.class));
    }

    @Test
    void testFindAllBorrowed() {
        Mockito.when(repository.findAllBorrowed(any(PageRequest.class))).thenReturn(page);
        cut.findAllBorrowed(2);
        Mockito.verify(repository).findAllBorrowed(any(PageRequest.class));
    }

    @Test
    void testSaveBook() {
        Mockito.when(repository.save(any(Book.class))).thenReturn(book);
        cut.save(book);
        Mockito.verify(repository).save(any(Book.class));
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void testBookExists(int id, boolean found) {
        Mockito.when(repository.existsById(id)).thenReturn(found);
        cut.existsById(id);
        Mockito.verify(repository).existsById(id);
    }
}