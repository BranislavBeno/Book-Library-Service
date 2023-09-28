package com.book.library.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

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
    public LocalStackContainer localStackContainer(DynamicPropertyRegistry registry) {
        try (var container = new LocalStackContainer(DockerImageName.parse("localstack/localstack-full:1.4.0"))) {
            container.withClasspathResourceMapping("/localstack", "/docker-entrypoint-initaws.d", BindMode.READ_ONLY)
                    .withEnv("USE_SINGLE_REGION", "true")
                    .withEnv("DEFAULT_REGION", "eu-central-1")
                    .withServices(LocalStackContainer.Service.SQS)
                    .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));
            container.start();

            registry.add("spring.cloud.aws.endpoint", () -> container.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
            registry.add("spring.cloud.aws.region.static", container::getRegion);
            registry.add("spring.cloud.aws.credentials.access-key", container::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", container::getSecretKey);

            return container;
        }
    }
}
