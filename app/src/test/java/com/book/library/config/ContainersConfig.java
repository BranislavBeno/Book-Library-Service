package com.book.library.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @RestartScope
    public KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        try (var container = new KeycloakContainer(
                DockerImageName.parse("quay.io/keycloak/keycloak:22.0.1").asCanonicalNameString())) {
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
    @ServiceConnection
    @RestartScope
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:12.15");
    }
}
