package com.book.library.recommendation;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record BookRecommendationListener(BookRecommendationService service) {

    private static final Logger LOG = LoggerFactory.getLogger(BookRecommendationListener.class);

    @SqsListener("${custom.sharing-queue}")
    public void listenToRecommendationMessages(BookRecommendationNotification payload) {
        LOG.info("Incoming book recommendation payload: {}", payload);
    }
}
