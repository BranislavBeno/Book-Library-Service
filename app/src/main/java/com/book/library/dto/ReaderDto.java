package com.book.library.dto;

public record ReaderDto(int id, String firstName, String lastName) implements DataTransferObject {

    @Override
    public String toString() {
        return "%s %s".formatted(firstName, lastName);
    }
}
