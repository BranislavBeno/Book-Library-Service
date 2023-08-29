package com.book.library.book;

import static org.mockito.ArgumentMatchers.any;

import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
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
    private BookRepository bookRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private Page<Book> bookPage;

    @Mock
    private Page<AvailableBookDto> availableBookPage;

    @Mock
    private Page<BorrowedBookDto> borrowedBookPage;

    @Mock
    private Book book;

    private BookService cut;

    @BeforeEach
    void setUp() {
        cut = new BookService(bookRepository, borrowedBookRepository, readerRepository, 5);
    }

    @Test
    void testFindAll() {
        Mockito.when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);
        cut.findAllBooks(1);
        Mockito.verify(bookRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testFindAllAvailable() {
        Mockito.when(bookRepository.findAllAvailableBooks(any(PageRequest.class)))
                .thenReturn(availableBookPage);
        cut.findAllAvailableBooks(0);
        Mockito.verify(bookRepository).findAllAvailableBooks(any(PageRequest.class));
    }

    @Test
    void testFindAllBorrowed() {
        Mockito.when(borrowedBookRepository.findAllBorrowedBooks(any(PageRequest.class)))
                .thenReturn(borrowedBookPage);
        cut.findAllBorrowedBooks(2);
        Mockito.verify(borrowedBookRepository).findAllBorrowedBooks(any(PageRequest.class));
    }

    @Test
    void testSaveBook() {
        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(book);
        cut.saveBook(book);
        Mockito.verify(bookRepository).save(any(Book.class));
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void testBookExists(long id, boolean found) {
        Mockito.when(bookRepository.existsById(id)).thenReturn(found);
        cut.bookExists(id);
        Mockito.verify(bookRepository).existsById(id);
    }

    @Test
    void testBookFound() {
        long id = 1;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(any(Book.class)));
        cut.findBook(id);
        Mockito.verify(bookRepository).findById(id);
    }

    @Test
    void testDeleteBook() {
        long id = 1;
        Mockito.doNothing().when(bookRepository).deleteById(id);
        cut.deleteBook(id);
        Mockito.verify(bookRepository).deleteById(id);
    }

    @Test
    void testBookBorrowing() {
        long bookId = 1;
        long readerId = 1;
        var reader = Mockito.mock(Reader.class);
        var borrowedBook = createBorrowedBook();
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);

        cut.borrowBook(bookId, readerId);

        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verify(readerRepository).findById(bookId);
        Mockito.verify(borrowedBookRepository).save(any(BorrowedBook.class));
    }

    private static Book createBook() {
        var book = new Book();
        book.setName("");
        book.setAuthor("");

        return book;
    }

    private static Reader createReader() {
        var reader = new Reader();
        reader.setFirstName("");
        reader.setLastName("");

        return reader;
    }

    private static BorrowedBook createBorrowedBook() {
        var borrowedBook = new BorrowedBook();
        borrowedBook.setId(1L);
        borrowedBook.setReader(createReader());
        borrowedBook.setBook(createBook());

        return borrowedBook;
    }
}
