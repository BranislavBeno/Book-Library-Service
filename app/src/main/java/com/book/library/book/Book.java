package com.book.library.book;

import com.book.library.recommendation.BookRecommendationRequest;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_generator")
    @SequenceGenerator(name = "book_generator", sequenceName = "book_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    private String name;

    private String author;

    @OneToOne(mappedBy = "book")
    private BorrowedBook borrowed;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "book_id")
    private List<BookRecommendationRequest> recommendationRequests = new ArrayList<>();

    public BorrowedBook getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(BorrowedBook borrowed) {
        this.borrowed = borrowed;
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

    public List<BookRecommendationRequest> getRecommendationRequests() {
        return recommendationRequests;
    }

    public void setRecommendationRequests(List<BookRecommendationRequest> recommendationRequests) {
        this.recommendationRequests = recommendationRequests;
    }

    public String bookInfo() {
        return "%s written by %s".formatted(name, author);
    }
}
