package com.book.library.recommendation;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record DefaultBookRecommendationListener(BookRecommendationService service)
        implements BookRecommendationListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBookRecommendationListener.class);

    @Override
    @SqsListener("${custom.sharing-queue}")
    public void listenToMessages(BookRecommendationNotification payload) {
        LOG.info("Incoming book recommendation payload: {}", payload);
    }
}
