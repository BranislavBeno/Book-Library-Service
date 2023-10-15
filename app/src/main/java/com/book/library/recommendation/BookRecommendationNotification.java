package com.book.library.recommendation;

public final class BookRecommendationNotification {

    private String recommencedEmail;
    private String recommencedName;
    private int recommencedId;
    private String bookInfo;
    private int bookId;
    private String token;

    public BookRecommendationNotification() {}

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

    public void setRecommencedEmail(String recommencedEmail) {
        this.recommencedEmail = recommencedEmail;
    }

    public String getRecommencedName() {
        return recommencedName;
    }

    public void setRecommencedName(String recommencedName) {
        this.recommencedName = recommencedName;
    }

    public int getRecommencedId() {
        return recommencedId;
    }

    public void setRecommencedId(int recommencedId) {
        this.recommencedId = recommencedId;
    }

    public String getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(String bookInfo) {
        this.bookInfo = bookInfo;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return """
                BookRecommendationNotification { +
                    recommencedEmail=%s
                    recommencedName=%s
                    recommencedId=%s
                    bookInfo=%s
                    bookId=%s
                    token=%s
                }"""
                .formatted(recommencedEmail, recommencedName, recommencedId, bookInfo, bookId, token);
    }
}
