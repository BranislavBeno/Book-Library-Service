package com.book.library.service;

import static org.mockito.ArgumentMatchers.any;

import com.book.library.domain.FileBook;
import com.book.library.repository.BookFileRepository;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class BookServiceTest implements WithAssertions {

    @Mock
    private BookFileRepository repository;

    @Mock
    private Page<FileBook> page;

    @Mock
    private FileBook book;

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
        Mockito.when(repository.save(any(FileBook.class))).thenReturn(book);
        cut.save(book);
        Mockito.verify(repository).save(any(FileBook.class));
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void testBookExists(int id, boolean found) {
        Mockito.when(repository.existsById(id)).thenReturn(found);
        cut.existsById(id);
        Mockito.verify(repository).existsById(id);
    }

    @Test
    void testBookFound() {
        int id = 1;
        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(any(FileBook.class)));
        cut.findById(id);
        Mockito.verify(repository).findById(id);
    }

    @Test
    void testDeleteBook() {
        int id = 1;
        Mockito.doNothing().when(repository).deleteById(id);
        cut.deleteById(id);
        Mockito.verify(repository).deleteById(id);
    }
}
