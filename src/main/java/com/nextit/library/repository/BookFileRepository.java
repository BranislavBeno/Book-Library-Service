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
import java.util.function.Predicate;

public final class BookFileRepository implements BookRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookFileRepository.class);

    private final List<Book> books;

    public BookFileRepository(String path) {
        this.books = initializeRepository(path);
    }

    private List<Book> initializeRepository(String path) {
        try {
            XmlMapper mapper = getXmlMapper();
            File file = new File(path);
            List<Book> list = mapper.readValue(file, new TypeReference<>() {
            });
            String message = "Input file read successfully. %d books imported.".formatted(list.size());
            logger.info(message);

            return list;
        } catch (Exception e) {
            String message = "Input file '%s' reading failed.".formatted(path);
            logger.error(message, e);
            throw new BookFileNotFoundException(message);
        }
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return provideBookPage(pageable, List.copyOf(books));
    }

    @Override
    public Page<Book> findAllAvailable(Pageable pageable) {
        Predicate<Book> predicate = b -> b.getBorrowed() != null && b.getBorrowed().from() == null;
        return provideBookPage(pageable, filterBooks(predicate));
    }

    @Override
    public List<Book> findAllBorrowed() {
        Predicate<Book> predicate = b -> b.getBorrowed() != null && b.getBorrowed().from() != null;
        return filterBooks(predicate);
    }

    public void exportToFile(String path) {
        try {
            XmlMapper xmlMapper = getXmlMapper();
            File file = new File(path);
            xmlMapper.writeValue(file, books);

            String message = "Output file saved successfully. %d books exported.".formatted(books.size());
            logger.info(message);
        } catch (Exception e) {
            String message = "Output file '%s' writing failed.".formatted(path);
            logger.error(message, e);
            throw new BookFileNotFoundException(message);
        }
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
}
