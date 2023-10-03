package com.book.library.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest(properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTestRepository {

    @ServiceConnection
    private static final PostgreSQLContainer<?> REPOSITORY_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"));

    static {
        REPOSITORY_CONTAINER.start();
    }
}
