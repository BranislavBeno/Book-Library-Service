package com.book.library.reader;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_reader.sql")
class ReaderRepositoryTest extends BaseRepositoryTest<Reader> implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    void testFindAll() {
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(2);
    }

    @Test
    void testFindById() {
        assertEntity(r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            assertThat(r.getLastName()).isEqualTo("First");
        });
    }

    @Test
    void testDeleteById() {
        repository.deleteById(1);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(1);
    }

    @Test
    void testAddReader() {
        Reader reader = createReader();
        repository.save(reader);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(3);
    }

    @Test
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
    protected JpaRepository<Reader, Integer> getRepository() {
        return repository;
    }
}
