package com.book.library.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersConfig.class);

    @Bean
    @ServiceConnection
    @RestartScope
    public PostgreSQLContainer<?> postgresSqlContainer() {
        return new PostgreSQLContainer<>("postgres:15.4");
    }

    @Bean
    @RestartScope
    public KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        try (var container = new KeycloakContainer(
                DockerImageName.parse("quay.io/keycloak/keycloak:22.0.3").asCanonicalNameString())) {
            container.withRealmImportFiles("keycloak/stratospheric-realm.json", "keycloak/stratospheric-users-0.json");
            container.start();

            registry.add("spring.security.oauth2.client.registration.cognito.logoutUrl", container::getAuthServerUrl);
            registry.add(
                    "spring.security.oauth2.client.provider.cognito.issuerUri",
                    () -> container.getAuthServerUrl() + "/realms/stratospheric");

            return container;
        }
    }

    @Bean
    @RestartScope
    public LocalStackContainer localStackContainer(DynamicPropertyRegistry registry)
            throws IOException, InterruptedException {
        try (var container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3.0"))) {
            container.withServices(LocalStackContainer.Service.SQS);
            container.start();

            Container.ExecResult createQueue =
                    container.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "bls-book-sharing");
            LOGGER.info("SQS queue creation finished with exit code {}.", createQueue.getExitCode());
            LOGGER.info(createQueue.getStdout());

            registry.add("spring.cloud.aws.endpoint", () -> container
                    .getEndpointOverride(LocalStackContainer.Service.SQS)
                    .toString());
            registry.add("spring.cloud.aws.region.static", container::getRegion);
            registry.add("spring.cloud.aws.credentials.access-key", container::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", container::getSecretKey);

            return container;
        }
    }
}
