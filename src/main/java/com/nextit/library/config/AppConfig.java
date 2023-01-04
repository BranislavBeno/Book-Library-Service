package com.nextit.library.config;

import com.nextit.library.repository.BookFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public BookFileRepository bookFileRepository(@Value("${book.repository.path}") String path) {
        return new BookFileRepository(path);
    }
}
