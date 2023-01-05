package com.nextit.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nextit.library.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public List<Book> findAll() {
        return books;
    }

    @Override
    public List<Book> findAllAvailable() {
        Predicate<Book> bookPredicate = b -> b.getBorrowed() != null && b.getBorrowed().from() == null;
        return filterBooks(bookPredicate);
    }

    @Override
    public List<Book> findAllBorrowed() {
        Predicate<Book> bookPredicate = b -> b.getBorrowed() != null && b.getBorrowed().from() != null;
        return filterBooks(bookPredicate);
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
}
