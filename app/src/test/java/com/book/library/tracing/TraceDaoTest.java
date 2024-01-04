package com.book.library.tracing;

import com.book.library.AbstractTestResources;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

class TraceDaoTest extends AbstractTestResources implements WithAssertions {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private TraceDao traceDao;

    @BeforeEach
    void setUp() {
        Stream.of(
                        new TracingEvent(this, "/book/all", "mike"),
                        new TracingEvent(this, "/reader/all", "duke"),
                        new TracingEvent(this, "/reader/all", "mike"))
                .forEach(publisher::publishEvent);
    }

    @AfterEach
    void tearDown() {
        traceDao.deleteItems();
    }

    @ParameterizedTest
    @CsvSource({"mike,2", "duke,1"})
    void testFindAllEventsForUser(String username, int count) {
        Awaitility.await().atMost(30, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            List<Breadcrumb> breadcrumbs = traceDao.findAllEventsForUser(username);
            assertThat(breadcrumbs).hasSize(count);
            Breadcrumb breadcrumb = breadcrumbs.stream().findFirst().orElse(null);
            assertThat(breadcrumb).isNotNull().satisfies(b -> assertThat(b.getUri())
                    .endsWith("/all"));
        });
    }

    @ParameterizedTest
    @CsvSource({"mike,2", "duke,1"})
    void testFindUserTraceForLastTwoWeeks(String username, int count) {
        Awaitility.await().atMost(30, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(() -> {
            List<Breadcrumb> breadcrumbs = traceDao.findUserTraceForLastTwoWeeks(username);
            assertThat(breadcrumbs).hasSize(count);
        });
    }
}
