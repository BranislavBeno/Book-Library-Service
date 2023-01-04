package com.nextit.library.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nextit.library.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookFileRepository {

    private static final Logger logger = LoggerFactory.getLogger(BookFileRepository.class);

    private List<Book> books;

    public BookFileRepository(String path) {
        initializeRepository(path);
    }

    private void initializeRepository(String path) {
        books = new ArrayList<>();
        try {
            File file = new File(path);
            XmlMapper mapper = XmlMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();
            List<Book> values = mapper.readValue(file, new TypeReference<>() {
            });
            books.addAll(values);

            String message = "Input file read successfully. %d books imported.".formatted(books.size());
            logger.info(message);
        } catch (Exception e) {
            String message = "Input file '%s' reading failed.".formatted(path);
            logger.error(message, e);
            throw new BookFileNotFoundException(message);
        }
    }

    public List<Book> findAll() {
        return books;
    }
}
