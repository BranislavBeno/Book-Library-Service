package com.book.library.recommendation;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public record TestBookRecommendationListener(BookRecommendationService service, DynamoDbClient client)
        implements BookRecommendationListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBookRecommendationListener.class);

    @Override
    @SqsListener("${custom.recommendation-queue}")
    public void listenToMessages(BookRecommendationNotification notification) {
        LOG.info("Got message with payload: {}", notification);

        TestNotification testNotification = toTestNotification(notification);

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
        enhancedClient
                .table("Notifications", TableSchema.fromBean(TestNotification.class))
                .putItem(testNotification);

        LOG.info("Payment details saved in table");
    }

    private TestNotification toTestNotification(BookRecommendationNotification notification) {
        TestNotification testNotification = new TestNotification();
        testNotification.setNotificationId("notification_123");
        testNotification.setEmail(notification.getRecommencedEmail());
        testNotification.setBookInfo(notification.getBookInfo());
        testNotification.setToken(notification.getToken());

        return testNotification;
    }
}
