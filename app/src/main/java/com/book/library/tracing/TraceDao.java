package com.book.library.tracing;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TraceDao {

    private static final Logger LOG = LoggerFactory.getLogger(TraceDao.class);

    private final DynamoDbTemplate dynamoDbTemplate;

    public TraceDao(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    @Async
    @EventListener(TracingEvent.class)
    public void storeTracingEvent(TracingEvent tracingEvent) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setId(UUID.randomUUID().toString());
        breadcrumb.setUri(tracingEvent.getUri());
        breadcrumb.setUsername(tracingEvent.getUsername());
        breadcrumb.setTimestamp(ZonedDateTime.now().toString());

        dynamoDbTemplate.save(breadcrumb);

        LOG.info("Successfully stored breadcrumb trace");
    }
}
