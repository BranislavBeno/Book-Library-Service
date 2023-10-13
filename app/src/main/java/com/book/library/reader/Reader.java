package com.book.library.reader;

import com.book.library.book.BorrowedBook;
import com.book.library.recommendation.BookRecommendationRequest;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reader_generator")
    @SequenceGenerator(name = "reader_generator", sequenceName = "reader_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    private String firstName;

    private String lastName;

    private String email;

    @OneToMany(mappedBy = "reader")
    private List<BorrowedBook> borrowedBooks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recommenced")
    private List<BookRecommendationRequest> bookRecommendationRequests = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BorrowedBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public List<BookRecommendationRequest> getBookRecommendationRequests() {
        return bookRecommendationRequests;
    }

    public void setBookRecommendationRequests(List<BookRecommendationRequest> bookRecommendationRequests) {
        this.bookRecommendationRequests = bookRecommendationRequests;
    }

    public String fullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
