package com.book.library.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRecommendationRequestRepository extends JpaRepository<BookRecommendationRequest, Integer> {}
