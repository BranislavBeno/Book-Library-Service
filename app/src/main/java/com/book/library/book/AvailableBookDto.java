package com.book.library.book;

public record AvailableBookDto(long id, String name, String author) {

    public AvailableBookDto(Book book) {
        this(book.getId(), book.getName(), book.getAuthor());
    }
}
