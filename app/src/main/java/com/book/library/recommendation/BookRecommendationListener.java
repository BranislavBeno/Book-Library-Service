package com.book.library.recommendation;

public interface BookRecommendationListener {

    void listenToMessages(BookRecommendationNotification notification);
}
