package com.book.library.recommendation;

import com.book.library.book.Book;
import com.book.library.reader.Reader;
import jakarta.persistence.*;

@Entity
public class BookRecommendationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_generator")
    @SequenceGenerator(name = "request_generator", sequenceName = "book_recommendation_request_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommenced_id")
    private Reader recommenced;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Reader getRecommenced() {
        return recommenced;
    }

    public void setRecommenced(Reader recommenced) {
        this.recommenced = recommenced;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
