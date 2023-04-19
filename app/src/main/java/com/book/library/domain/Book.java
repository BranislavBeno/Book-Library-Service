package com.book.library.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {

    private int id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Author")
    private String author;

    @JsonProperty("Borrowed")
    private Borrowed borrowed;

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

    public Borrowed getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Borrowed borrowed) {
        this.borrowed = borrowed;
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s']".formatted(id, name, author);
    }
}
