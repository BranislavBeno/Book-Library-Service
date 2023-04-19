package com.book.library.dto;

public record AnyBookDto(int id, String name, String author, boolean borrowed) implements BookDto {}
