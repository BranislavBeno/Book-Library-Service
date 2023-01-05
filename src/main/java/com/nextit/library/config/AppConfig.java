package com.nextit.library.config;

import com.nextit.library.dto.BookMapper;
import com.nextit.library.repository.BookFileRepository;
import com.nextit.library.repository.BookRepository;
import com.nextit.library.service.BookService;
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
    public BookService bookService(@Autowired BookRepository repository) {
        return new BookService(repository);
    }

    @Bean
    public BookMapper bookMapper() {
        return new BookMapper();
    }
}
