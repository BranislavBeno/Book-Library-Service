package com.book.library.repository;

import com.book.library.domain.FileBook;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class BookFileRepositoryImpl implements BookFileRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookFileRepositoryImpl.class);

    private final List<FileBook> books;
    private final AtomicInteger identifier;

    public BookFileRepositoryImpl(String path) {
        InitialData data = initialize(path);

        this.books = data.books;
        this.identifier = data.id;
    }

    private InitialData initialize(String path) {
        try {
            XmlMapper mapper = getXmlMapper();
            File file = new File(path);
            List<FileBook> list = mapper.readValue(file, new TypeReference<>() {});
            int maxId = list.stream().mapToInt(FileBook::getId).max().orElse(0);

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
    public Page<FileBook> findAll(Pageable pageable) {
        return provideBookPage(pageable, List.copyOf(books));
    }

    @Override
    public Page<FileBook> findAllAvailable(Pageable pageable) {
        Predicate<FileBook> predicate =
                b -> b.getBorrowed() == null || b.getBorrowed().from() == null;
        return provideBookPage(pageable, filterBooks(predicate));
    }

    @Override
    public Page<FileBook> findAllBorrowed(Pageable pageable) {
        Predicate<FileBook> predicate =
                b -> b.getBorrowed() != null && b.getBorrowed().from() != null;
        return provideBookPage(pageable, filterBooks(predicate));
    }

    @Override
    public FileBook save(FileBook entity) {
        try {
            FileBook book = findById(entity.getId()).orElseThrow();
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
    public Optional<FileBook> findById(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst();
    }

    @Override
    public void deleteById(int id) {
        FileBook book = findById(id).orElseThrow();
        books.remove(book);
    }

    private List<FileBook> filterBooks(Predicate<FileBook> predicate) {
        return books.stream().filter(predicate).toList();
    }

    private static XmlMapper getXmlMapper() {
        return XmlMapper.builder().addModule(new JavaTimeModule()).build();
    }

    private PageImpl<FileBook> provideBookPage(Pageable pageable, List<FileBook> books) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<FileBook> list;

        if (books.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, books.size());
            list = books.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), books.size());
    }

    private record InitialData(List<FileBook> books, AtomicInteger id) {}
}
