package com.book.library.recommendation;

import com.book.library.AbstractTestResources;
import com.book.library.book.BookRepository;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.WithAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.jdbc.Sql;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Sql(scripts = "/sql/init_db.sql")
class BookRecommendationServiceIntegrationTest extends AbstractTestResources implements WithAssertions {

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookRecommendationService service;

    @Autowired
    private DynamoDbClient dynamoDbClient;

    @Test
    void testQueue() {
        int bookId = 1;

        assertRequestsSize(bookId, 1);

        service.recommendBookTo(bookId, 3);

        assertRequestsSize(bookId, 2);
        assertRequestProcessing();
    }

    private void assertRequestsSize(int bookId, int expected) {
        repository
                .findBookById(bookId)
                .ifPresentOrElse(
                        b -> assertThat(b.getRecommendationRequests()).hasSize(expected), () -> fail("Book not found"));
    }

    private void assertRequestProcessing() {
        PageIterable<TestNotification> notifications = getNotifications();

        Awaitility.await().atMost(30, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            Iterable<TestNotification> items = notifications.items();
            assertThat(items).hasSize(1);
            assertThat(items.iterator().next().getNotificationId()).isEqualTo("notification_123");
        });
    }

    private PageIterable<TestNotification> getNotifications() {
        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();

        return enhancedClient
                .table("Notifications", TableSchema.fromBean(TestNotification.class))
                .scan();
    }

    @TestConfiguration
    static class BookRecommendationTestConfig {

        @Bean
        @ConditionalOnProperty(prefix = "custom", name = "use-real-sqs-listener", havingValue = "false")
        public BookRecommendationListener listener(
                @Autowired BookRecommendationService service, @Autowired DynamoDbClient client) {
            return new TestBookRecommendationListener(service, client);
        }
    }
}
