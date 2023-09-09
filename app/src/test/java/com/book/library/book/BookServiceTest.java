package com.book.library.book;

import static org.mockito.ArgumentMatchers.*;

import com.book.library.dto.AvailableBookDto;
import com.book.library.dto.BorrowedBookDto;
import com.book.library.dto.BorrowedDto;
import com.book.library.dto.ReaderDto;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
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
    private Page<ReaderDto> readerPage;

    @Mock
    private Book book;

    private BookService cut;

    @BeforeEach
    void setUp() {
        cut = new BookService(bookRepository, borrowedBookRepository, readerRepository, 5);
    }

    @Test
    void testFindBooks() {
        Mockito.when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);
        cut.findBooks(1);
        Mockito.verify(bookRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testFindAvailableBooks() {
        Mockito.when(bookRepository.findAllAvailableBooks(any(PageRequest.class)))
                .thenReturn(availableBookPage);
        cut.findAvailableBooks(1);
        Mockito.verify(bookRepository).findAllAvailableBooks(any(PageRequest.class));
    }

    @Test
    void testFindBorrowedBooks() {
        Mockito.when(borrowedBookRepository.findAllBorrowedBooks(any(PageRequest.class)))
                .thenReturn(borrowedBookPage);
        cut.findBorrowedBooks(1);
        Mockito.verify(borrowedBookRepository).findAllBorrowedBooks(any(PageRequest.class));
    }

    @Test
    void testFindReaders() {
        Mockito.when(readerRepository.findAllReaders(any(PageRequest.class))).thenReturn(readerPage);
        cut.findReaders(1);
        Mockito.verify(readerRepository).findAllReaders(any(PageRequest.class));
    }

    @Test
    void testSaveBook() {
        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(book);
        cut.saveBook(book);
        Mockito.verify(bookRepository).save(any(Book.class));
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void testBookExists(int id, boolean found) {
        Mockito.when(bookRepository.existsById(id)).thenReturn(found);
        cut.bookExists(id);
        Mockito.verify(bookRepository).existsById(id);
    }

    @Test
    void testBookFound() {
        int id = 1;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(any(Book.class)));
        cut.findBook(id);
        Mockito.verify(bookRepository).findById(id);
    }

    @Test
    void testDeleteBook() {
        int id = 1;
        Mockito.doNothing().when(bookRepository).deleteById(id);
        cut.deleteBook(id);
        Mockito.verify(bookRepository).deleteById(id);
    }

    @Test
    void testNotExistingBookAvailing() {
        int borrowedBookId = 1;
        Mockito.when(borrowedBookRepository.findById(borrowedBookId)).thenReturn(Optional.empty());

        AvailableBookDto result = cut.availBook(borrowedBookId);
        assertThat(result).isNull();

        Mockito.verify(borrowedBookRepository).findById(anyInt());
        Mockito.verifyNoMoreInteractions(borrowedBookRepository);
    }

    @Test
    void testBookAvailing() {
        int borrowedBookId = 1;
        var borrowedBook = Mockito.mock(BorrowedBook.class);
        Mockito.when(borrowedBookRepository.findById(borrowedBookId)).thenReturn(Optional.of(borrowedBook));
        Mockito.when(borrowedBook.getBook()).thenReturn(book);
        Mockito.when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));

        AvailableBookDto result = cut.availBook(borrowedBookId);
        assertThat(result).isNotNull();

        Mockito.verify(borrowedBookRepository).findById(anyInt());
        Mockito.verify(borrowedBook).getBook();
        Mockito.verify(bookRepository).findById(anyInt());
        Mockito.verify(borrowedBookRepository).deleteById(anyInt());
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void testBookBorrowingWithIncompleteParameters(Book bookParam, Reader readerParam) {
        int bookId = 1;
        int readerId = 1;
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.ofNullable(bookParam));
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.ofNullable(readerParam));

        BorrowedDto dto = new BorrowedDto(bookId, readerId);
        BorrowedBookDto result = cut.borrowBook(dto);
        assertThat(result).isNull();

        Mockito.verify(bookRepository).findById(bookId);
        Mockito.verify(readerRepository).findById(bookId);
        Mockito.verifyNoInteractions(borrowedBookRepository);
    }

    public static Stream<Arguments> parameters() {
        return Stream.of(Arguments.of(new Book(), null), Arguments.of(null, new Reader()));
    }

    @Test
    void testBookBorrowing() {
        int bookId = 1;
        int readerId = 1;
        var reader = Mockito.mock(Reader.class);
        var borrowedBook = createBorrowedBook();
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(readerRepository.findById(readerId)).thenReturn(Optional.of(reader));
        Mockito.when(borrowedBookRepository.save(any(BorrowedBook.class))).thenReturn(borrowedBook);

        BorrowedDto dto = new BorrowedDto(bookId, readerId);
        BorrowedBookDto result = cut.borrowBook(dto);
        assertThat(result).isNotNull();

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
        borrowedBook.setId(1);
        borrowedBook.setReader(createReader());
        borrowedBook.setBook(createBook());

        return borrowedBook;
    }
}
