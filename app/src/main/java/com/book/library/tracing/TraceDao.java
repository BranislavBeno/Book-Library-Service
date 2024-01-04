package com.book.library.tracing;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class TraceDao {

    private static final Logger LOG = LoggerFactory.getLogger(TraceDao.class);

    private final DynamoDbTable<Breadcrumb> dynamoDbTable;

    public TraceDao(DynamoDbClient dynamoDbClient, String tableName) {
        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        this.dynamoDbTable = enhancedClient.table(tableName, TableSchema.fromBean(Breadcrumb.class));
    }

    @Async
    @EventListener(TracingEvent.class)
    public void storeTracingEvent(TracingEvent tracingEvent) {
        var breadcrumb = new Breadcrumb();
        breadcrumb.setId(UUID.randomUUID().toString());
        breadcrumb.setUri(tracingEvent.getUri());
        breadcrumb.setUsername(tracingEvent.getUsername());
        breadcrumb.setTimestamp(ZonedDateTime.now().toString());

        dynamoDbTable.putItem(breadcrumb);

        LOG.info("Successfully stored breadcrumb trace");
    }

    List<Breadcrumb> findAllEventsForUser(String username) {
        return getItems().filter(b -> b.getUsername().equals(username)).toList();
    }

    List<Breadcrumb> findUserTraceForLastTwoWeeks(String username) {
        var twoWeeksAgo = ZonedDateTime.now().minusWeeks(2);

        Predicate<Breadcrumb> predicate = b -> {
            ZonedDateTime timestamp = ZonedDateTime.parse(b.getTimestamp());
            return b.getUsername().equals(username) && twoWeeksAgo.isBefore(timestamp);
        };

        return getItems().filter(predicate).toList();
    }

    void deleteItems() {
        getItems().forEach(dynamoDbTable::deleteItem);
    }

    private Stream<Breadcrumb> getItems() {
        return dynamoDbTable.scan().items().stream();
    }
}
