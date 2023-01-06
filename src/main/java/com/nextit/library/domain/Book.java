package com.nextit.library.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @JsonProperty("Name")
    private String name;
    @NotEmpty
    @JsonProperty("Author")
    private String author;
    @NotNull
    @Convert(converter = BorrowedConverter.class)
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
