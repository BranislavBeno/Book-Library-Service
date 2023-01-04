package com.nextit.library.repository;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookFileRepositoryTest implements WithAssertions {

    @Autowired
    private BookFileRepository cut;

    @Test
    void testNonEmptyListOfBooks() {
        assertThat(cut.findAll()).hasSize(1);
    }
}