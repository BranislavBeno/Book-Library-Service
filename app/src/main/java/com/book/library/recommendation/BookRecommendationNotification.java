package com.book.library.recommendation;

public final class BookRecommendationNotification {

    private final String recommencedEmail;
    private final String recommencedName;
    private final int recommencedId;
    private final String bookInfo;
    private final int bookId;
    private final String token;

    public BookRecommendationNotification(BookRecommendationRequest request) {
        this.recommencedEmail = request.getRecommenced().getEmail();
        this.recommencedName = request.getRecommenced().fullName();
        this.recommencedId = request.getRecommenced().getId();
        this.bookInfo = request.getBook().bookInfo();
        this.bookId = request.getBook().getId();
        this.token = request.getToken();
    }

    public String getRecommencedEmail() {
        return recommencedEmail;
    }

    public String getRecommencedName() {
        return recommencedName;
    }

    public int getRecommencedId() {
        return recommencedId;
    }

    public String getBookInfo() {
        return bookInfo;
    }

    public int getBookId() {
        return bookId;
    }

    public String getToken() {
        return token;
    }
}
