package com.book.library;

import com.book.library.config.ContainersConfig;
import org.springframework.boot.SpringApplication;

public class LibraryTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(LibraryApplication::main)
                .with(ContainersConfig.class)
                .run(args);
    }
}
