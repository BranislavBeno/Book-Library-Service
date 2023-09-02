package com.book.library.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AvailableBookDto(
        long id, @NotEmpty @Size(max = 15, message = "Book name too long.") String name, @NotEmpty String author)
        implements BookDto {

    public AvailableBookDto(Book book) {
        this(book.getId(), book.getName(), book.getAuthor());
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s']".formatted(id, name, author);
    }
}
