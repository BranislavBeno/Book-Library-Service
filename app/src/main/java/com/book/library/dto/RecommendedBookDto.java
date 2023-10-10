package com.book.library.dto;

public record RecommendedBookDto(
        String bookName, String bookAuthor, String readerFirstName, String readerLastName, String readerEmail) {

    public String book() {
        return "%s from %s".formatted(bookName(), bookAuthor());
    }

    public String readerName() {
        return "%s %s".formatted(readerFirstName(), readerLastName());
    }
}
