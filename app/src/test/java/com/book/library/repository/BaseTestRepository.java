package com.book.library.repository;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest(properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTestRepository {

    @ServiceConnection
    private static final PostgreSQLContainer REPOSITORY_CONTAINER =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18.1"));

    static {
        REPOSITORY_CONTAINER.start();
    }
}
