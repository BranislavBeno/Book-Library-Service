package com.book.library.dto;

public record ReaderDto(int id, String firstName, String lastName) implements DataTransferObject {}
