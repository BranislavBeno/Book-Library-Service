package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import com.book.library.repository.AbstractTestRepository;
import org.assertj.core.api.WithAssertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/init_db.sql")
class ReaderRepositoryTest extends AbstractTestRepository<Reader> implements WithAssertions {

    @Autowired
    private ReaderRepository repository;

    @Test
    void testFindAllReaders() {
        assertThat(repository.findAllReaders()).hasSize(7);
    }

    @Test
    void testFindAllReadersPaged() {
        Page<ReaderDto> readers = repository.findAllReadersPaged(getPageRequest());

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
    void testFindReaderById() {
        repository
                .findReaderById(1)
                .ifPresentOrElse(
                        r -> {
                            assertThat(r.readerName()).isEqualTo("Ján Prvý");
                            assertThat(r.getEmail()).isEqualTo("jan@b-l-s.click");
                        },
                        () -> fail("Reader not found"));
    }

    @Test
    void testFindReaderByEmail() {
        repository
                .findByEmail("juraj@b-l-s.click")
                .ifPresentOrElse(
                        r -> {
                            assertThat(r.getId()).isEqualTo(7);
                            assertThat(r.fullName()).isEqualTo("Juraj Siedmy");
                        },
                        () -> fail("Reader not found"));
    }

    @Test
    void testDeleteById() {
        assertThat(repository.findAll()).hasSize(7);

        repository.deleteById(5);

        assertThat(repository.findAll()).hasSize(6);
    }

    @Test
    void testAddReader() {
        assertThat(repository.findAll()).hasSize(7);

        Reader reader = createReader();
        repository.save(reader);

        assertThat(repository.findAll()).hasSize(8);
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

    @NonNull
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
