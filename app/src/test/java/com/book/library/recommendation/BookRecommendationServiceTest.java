package com.book.library.recommendation;

import com.book.library.book.BookRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookRecommendationServiceTest extends RecommendationTestResources implements WithAssertions {

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookRecommendationService service;

    @Test
    void testQueue() {
        int bookId = 1;

        assertRequestsSize(bookId, 1);

        service.recommendBookTo(bookId, 3);

        assertRequestsSize(bookId, 2);
    }

    private void assertRequestsSize(int bookId, int expected) {
        repository
                .findBookById(bookId)
                .ifPresentOrElse(
                        b -> assertThat(b.getRecommendationRequests()).hasSize(expected), () -> fail("Book not found"));
    }
}
