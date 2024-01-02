package com.book.library.tracing;

import com.book.library.AbstractTestResources;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.WithAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class TraceDaoTest extends AbstractTestResources implements WithAssertions {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private TraceDao traceDao;

    @BeforeEach
    void setUp() {
        TracingEvent event1 = new TracingEvent(this, "/book/all", "mike");
        TracingEvent event2 = new TracingEvent(this, "/reader/all", "duke");
        publisher.publishEvent(event1);
        publisher.publishEvent(event2);
    }

    @Test
    void testFindAllEventsForUser() {
        Awaitility.await().atMost(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            List<Breadcrumb> breadcrumbs = traceDao.findAllEventsForUser("mike");
            assertThat(breadcrumbs).hasSize(1);
            Breadcrumb breadcrumb = breadcrumbs.stream().findFirst().orElse(null);
            assertThat(breadcrumb).isNotNull().satisfies(b -> assertThat(b.getUri())
                    .isEqualTo("/book/all"));
        });
    }
}
