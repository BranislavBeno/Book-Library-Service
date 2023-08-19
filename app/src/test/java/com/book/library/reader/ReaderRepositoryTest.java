package com.book.library.reader;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

class ReaderRepositoryTest extends BaseRepositoryTest implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testFindAll() {
        List<Reader> readers = repository.findAll();

        assertThat(readers).hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testFindById() {
        assertReader(r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            assertThat(r.getLastName()).isEqualTo("First");
        });
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testDeleteById() {
        repository.deleteById(1L);
        List<Reader> readers = repository.findAll();

        assertThat(readers).hasSize(1);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testAddReader() {
        Reader reader = createReader();
        repository.save(reader);
        List<Reader> readers = repository.findAll();

        assertThat(readers).hasSize(3);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testUpdateReader() {
        assertReader(r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            r.setFirstName("Thomas");
            repository.save(r);
        });

        assertReader(r -> assertThat(r.getFirstName()).isEqualTo("Thomas"));
    }

    private void assertReader(Consumer<Reader> consumer) {
        Optional<Reader> reader = repository.findById(1L);
        reader.ifPresentOrElse(consumer, () -> fail("Reader not found"));
    }

    @NotNull
    private static Reader createReader() {
        var reader = new Reader();
        reader.setFirstName("Mathias");
        reader.setLastName("Third");

        return reader;
    }
}
