package com.book.library.repository;

import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest(properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest<T> {

    @ServiceConnection
    private static final PostgreSQLContainer<?> REPOSITORY_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:12.15"));

    static {
        REPOSITORY_CONTAINER.start();
    }

    protected abstract JpaRepository<T, Integer> getRepository();

    protected void assertEntity(int id, Consumer<T> consumer) {
        Optional<T> reader = getRepository().findById(id);
        reader.ifPresentOrElse(consumer, () -> Assertions.fail("Entity not found"));
    }

    protected static PageRequest getPageRequest() {
        return PageRequest.of(0, 5);
    }
}
