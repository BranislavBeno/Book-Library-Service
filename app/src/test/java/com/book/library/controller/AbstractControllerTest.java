package com.book.library.controller;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
abstract class AbstractControllerTest {

    static final KeycloakContainer keycloak;

    static {
        keycloak = new KeycloakContainer(DockerImageName.parse("quay.io/keycloak/keycloak:22.0.3")
                        .asCanonicalNameString())
                .withRealmImportFile("keycloak/stratospheric-realm.json");
        keycloak.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.registration.cognito.logoutUrl", keycloak::getAuthServerUrl);
        registry.add(
                "spring.security.oauth2.client.provider.cognito.issuerUri",
                () -> keycloak.getAuthServerUrl() + "/realms/stratospheric");
    }
}
