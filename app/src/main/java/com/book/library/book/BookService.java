package com.book.library.book;

import com.book.library.dto.*;
import com.book.library.reader.Reader;
import com.book.library.reader.ReaderRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record BookService(
        BookRepository bookRepository,
        BorrowedBookRepository borrowedBookRepository,
        ReaderRepository readerRepository,
        int pageSize) {

    public Page<AnyBookDto> findBooks(int page) {
        return bookRepository.findAll(getPageRequest(page)).map(this::toAnyBookDto);
    }

    public Page<AvailableBookDto> findAvailableBooks(int page) {
        return bookRepository.findAllAvailableBooks(getPageRequest(page));
    }

    public Page<BorrowedBookDto> findBorrowedBooks(int page) {
        return borrowedBookRepository.findAllBorrowedBooks(getPageRequest(page));
    }

    public List<ReaderDto> findAllReaders() {
        return readerRepository.findAllReaders();
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public boolean bookExists(int id) {
        return bookRepository.existsById(id);
    }

    @Nullable
    public Book findBook(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    @Nullable
    public AvailableBookDto availBook(int id) {
        Optional<BorrowedBook> borrowedBook = borrowedBookRepository.findById(id);
        Optional<Book> book = borrowedBook.map(BorrowedBook::getBook);

        return book.flatMap(b -> {
                    borrowedBookRepository.deleteById(id);
                    return bookRepository.findById(b.getId()).map(AvailableBookDto::new);
                })
                .orElse(null);
    }

    @Nullable
    public BorrowedBookDto borrowBook(BorrowedDto dto) {
        Optional<BorrowedBook> borrowedBook = createBorrowedBook(dto);

        return borrowedBook
                .map(borrowedBookRepository::save)
                .map(BorrowedBookDto::new)
                .orElse(null);
    }

    private Optional<BorrowedBook> createBorrowedBook(BorrowedDto dto) {
        Optional<Book> book = bookRepository.findById(dto.getBookId());
        Optional<Reader> reader = readerRepository.findById(dto.getReaderId());

        return reader.flatMap(r -> book.map(b -> {
            BorrowedBook bb = new BorrowedBook();
            bb.setBook(b);
            bb.setReader(r);
            bb.setBorrowedOn(dto.getFrom());

            return bb;
        }));
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }

    private AnyBookDto toAnyBookDto(Book book) {
        int id = book.getId();
        String name = book.getName();
        String author = book.getAuthor();
        boolean borrowed = false;

        List<ReaderDto> offered = readerRepository.findAllReaders();
        Reader reader = book.getBorrowed() != null ? book.getBorrowed().getReader() : null;
        if (reader != null) {
            borrowed = true;
            ReaderDto readerDto = new ReaderDto(reader);
            offered.remove(readerDto);
        }

        return new AnyBookDto(id, name, author, borrowed, offered);
    }
}
