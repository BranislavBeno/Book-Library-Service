package com.nextit.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nextit.library.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public final class BookFileRepository implements BookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookFileRepository.class);

    private final List<Book> books;
    private final AtomicInteger identifier;

    public BookFileRepository(String path) {
        InitialData data = initialize(path);

        this.books = data.books;
        this.identifier = data.id;
    }

    private InitialData initialize(String path) {
        try {
            XmlMapper mapper = getXmlMapper();
            File file = new File(path);
            List<Book> list = mapper.readValue(file, new TypeReference<>() {
            });
            int maxId = list.stream()
                    .mapToInt(Book::getId)
                    .max()
                    .orElse(0);

            String message = "Input file read successfully. %d books imported.".formatted(list.size());
            LOGGER.info(message);

            return new InitialData(list, new AtomicInteger(maxId));
        } catch (Exception e) {
            String message = "Input file '%s' reading failed.".formatted(path);
            LOGGER.error(message, e);
            throw new BookFileNotFoundException(message);
        }
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return provideBookPage(pageable, List.copyOf(books));
    }

    @Override
    public Page<Book> findAllAvailable(Pageable pageable) {
        Predicate<Book> predicate = b -> b.getBorrowed() == null || b.getBorrowed().from() == null;
        return provideBookPage(pageable, filterBooks(predicate));
    }

    @Override
    public Page<Book> findAllBorrowed(Pageable pageable) {
        Predicate<Book> predicate = b -> b.getBorrowed() != null && b.getBorrowed().from() != null;
        return provideBookPage(pageable, filterBooks(predicate));
    }

    @Override
    public Book save(Book entity) {
        try {
            Book book = findById(entity.getId()).orElseThrow();
            book.setAuthor(entity.getAuthor());
            book.setName(entity.getName());
            book.setBorrowed(entity.getBorrowed());

            return book;
        } catch (Exception e) {
            entity.setId(identifier.incrementAndGet());
            books.add(entity);

            return entity;
        }
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }

    @Override
    public Optional<Book> findById(int id) {
        return books.stream()
                .filter(b -> b.getId() == id)
                .findFirst();
    }

    @Override
    public void deleteById(int id) {
        Book book = findById(id).orElseThrow();
        books.remove(book);
    }

    private List<Book> filterBooks(Predicate<Book> predicate) {
        return books.stream()
                .filter(predicate)
                .toList();
    }

    private static XmlMapper getXmlMapper() {
        return XmlMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    private PageImpl<Book> provideBookPage(Pageable pageable, List<Book> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Book> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
    }

    private record InitialData(List<Book> books, AtomicInteger id) {
    }
}
