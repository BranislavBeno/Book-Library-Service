package com.book.library.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public final class AvailableBookDto implements BookDto {
    private int id;
    @NotEmpty
    @Size(max = 15, message = "Book name too long.")
    private String name;
    @NotEmpty
    private String author;

    public AvailableBookDto(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    public AvailableBookDto() {
        this(0, "", "");
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
}
