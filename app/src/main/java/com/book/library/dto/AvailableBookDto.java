package com.book.library.dto;

import com.book.library.book.Book;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public final class AvailableBookDto implements DataTransferObject {

    private int id;
    private @NotEmpty @Size(max = 15, message = "Book name too long.") String name;
    private @NotEmpty String author;

    public AvailableBookDto(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public AvailableBookDto() {
        this(0, null, null);
    }

    public AvailableBookDto(Book book) {
        this(book.getId(), book.getName(), book.getAuthor());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s']".formatted(id, name, author);
    }
}
