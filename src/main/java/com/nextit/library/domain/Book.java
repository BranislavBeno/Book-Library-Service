package com.nextit.library.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @JsonProperty("Name")
    private String name;
    @NotNull
    @JsonProperty("Author")
    private String author;
    @Convert(converter = BorrowedConverter.class)
    @JsonProperty("Borrowed")
    private Borrowed borrowed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
