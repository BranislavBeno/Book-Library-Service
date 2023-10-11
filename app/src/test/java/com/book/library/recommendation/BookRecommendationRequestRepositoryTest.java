package com.book.library.recommendation;

import com.book.library.repository.AbstractTestRepository;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

class BookRecommendationRequestRepositoryTest extends AbstractTestRepository<BookRecommendationRequest>
        implements WithAssertions {

    @Autowired
    private BookRecommendationRequestRepository repository;

    @Test
    void testRepository() {
        assertThat(repository).isNotNull();
    }

    @Override
    protected JpaRepository<BookRecommendationRequest, Integer> getRepository() {
        return repository;
    }
}
