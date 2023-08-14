package com.book.library.book;

import jakarta.persistence.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String author;

    @OneToOne(mappedBy = "book")
    private BorrowedBook borrowed;

    public BorrowedBook getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(BorrowedBook borrowed) {
        this.borrowed = borrowed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
