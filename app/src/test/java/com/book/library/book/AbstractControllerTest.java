package com.book.library.book;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractControllerTest {

    @ServiceConnection
    private static final PostgreSQLContainer<?> REPOSITORY_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:12.15"));

    private static final KeycloakContainer KEYCLOAK_CONTAINER;

    static {
        REPOSITORY_CONTAINER.start();

        KEYCLOAK_CONTAINER = new KeycloakContainer(DockerImageName.parse("quay.io/keycloak/keycloak:22.0.1")
                        .asCanonicalNameString())
                .withRealmImportFile("keycloak/stratospheric-realm.json");
        KEYCLOAK_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.client.registration.cognito.logoutUrl", KEYCLOAK_CONTAINER::getAuthServerUrl);
        registry.add(
                "spring.security.oauth2.client.provider.cognito.issuerUri",
                () -> KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/stratospheric");
    }
}
