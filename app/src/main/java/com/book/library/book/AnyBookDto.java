package com.book.library.book;

public record AnyBookDto(int id, String name, String author, boolean borrowed) implements BookDto {}
