package com.book.library.book;

public record AnyBookDto(long id, String name, String author, boolean borrowed) implements BookDto {}
