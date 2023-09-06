package com.book.library.filedto;

public record AnyBookDto(int id, String name, String author, boolean borrowed) implements BookDto {}
