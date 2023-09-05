package com.book.library.reader;

import com.book.library.repository.BaseRepositoryTest;
import jakarta.validation.constraints.NotNull;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class ReaderRepositoryTest extends BaseRepositoryTest<Reader> implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    void testFindAll() {
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(5);
    }

    @Test
    void testFindById() {
        assertEntity(5, r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            assertThat(r.getLastName()).isEqualTo("Piaty");
        });
    }

    @Test
    void testDeleteById() {
        repository.deleteById(5);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(4);
    }

    @Test
    void testAddReader() {
        Reader reader = createReader();
        repository.save(reader);
        Page<Reader> readers = repository.findAll(getPageRequest());

        assertThat(readers).hasSize(5);
    }

    @Test
    void testUpdateReader() {
        assertEntity(5, r -> {
            assertThat(r.getFirstName()).isEqualTo("Peter");
            r.setFirstName("Thomas");
            repository.save(r);
        });

        assertEntity(5, r -> assertThat(r.getFirstName()).isEqualTo("Thomas"));
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
