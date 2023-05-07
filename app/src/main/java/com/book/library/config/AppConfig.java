package com.book.library.config;

import com.book.library.dto.BookMapper;
import com.book.library.repository.BookFileRepository;
import com.book.library.repository.BookRepository;
import com.book.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookRepository bookRepository(@Value("${book.repository.path}") String path) {
        return new BookFileRepository(path);
    }

    @Bean
    public BookService bookService(
            @Autowired BookRepository repository, @Value("${book.service.page.size:20}") int pageSize) {
        return new BookService(repository, pageSize);
    }

    @Bean
    public BookMapper bookMapper() {
        return new BookMapper();
    }
}
