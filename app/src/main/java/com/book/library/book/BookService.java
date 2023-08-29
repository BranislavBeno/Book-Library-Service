package com.book.library.book;

import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record BookService(
        BookRepository bookRepository,
        BorrowedBookRepository borrowedBookRepository,
        ReaderRepository readerRepository,
        int pageSize) {

    public Page<Book> findAllBooks(int page) {
        return bookRepository.findAll(getPageRequest(page));
    }

    public Page<AvailableBookDto> findAllAvailableBooks(int page) {
        return bookRepository.findAllAvailableBooks(getPageRequest(page));
    }

    public Page<BorrowedBookDto> findAllBorrowedBooks(int page) {
        return borrowedBookRepository.findAllBorrowedBooks(getPageRequest(page));
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public boolean bookExists(long id) {
        return bookRepository.existsById(id);
    }

    @Nullable
    public Book findBook(long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void deleteBook(long id) {
        bookRepository.deleteById(id);
    }

    @Nullable
    public BorrowedBookDto borrowBook(long bookId, long readerId) {
        Optional<BorrowedBook> borrowedBook = createBorrowedBook(bookId, readerId);

        return borrowedBook
                .map(borrowedBookRepository::save)
                .map(BorrowedBookDto::new)
                .orElse(null);
    }

    private Optional<BorrowedBook> createBorrowedBook(long bookId, long readerId) {
        Optional<Book> book = bookRepository.findById(bookId);
        Optional<Reader> reader = readerRepository.findById(readerId);

        return reader.flatMap(r -> book.map(b -> {
            BorrowedBook bb = new BorrowedBook();
            bb.setBook(b);
            bb.setReader(r);
            return bb;
        }));
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
