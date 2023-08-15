package com.book.library.reader;

import com.book.library.repository.BaseRepositoryTest;
import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReaderRepositoryTest extends BaseRepositoryTest implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    void testBasic() {
        List<Reader> readers = repository.findAll();

        assertThat(readers).isEmpty();
    }
}
