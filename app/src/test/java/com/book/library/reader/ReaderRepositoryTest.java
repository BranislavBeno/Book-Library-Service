package com.book.library.reader;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

class ReaderRepositoryTest extends BaseRepositoryTest<Reader> implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testFindAll() {
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(2);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testFindById() {
        assertEntity(r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            assertThat(r.getLastName()).isEqualTo("First");
        });
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testDeleteById() {
        repository.deleteById(1L);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(1);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testAddReader() {
        Reader reader = createReader();
        repository.save(reader);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(3);
    }

    @Test
    @Sql(scripts = "/sql/init_reader.sql")
    void testUpdateReader() {
        assertEntity(r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            r.setFirstName("Thomas");
            repository.save(r);
        });

        assertEntity(r -> assertThat(r.getFirstName()).isEqualTo("Thomas"));
    }

    @NotNull
    private static Reader createReader() {
        var reader = new Reader();
        reader.setFirstName("Mathias");
        reader.setLastName("Third");

        return reader;
    }

    @Override
    protected JpaRepository<Reader, Long> getRepository() {
        return repository;
    }
}
