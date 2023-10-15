package com.book.library.recommendation;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@Testcontainers(disabledWithoutDocker = true)
@Sql(scripts = "/sql/init_db.sql")
abstract class RecommendationTestResources {

    @ServiceConnection
    private static final PostgreSQLContainer<?> REPOSITORY_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.4"));

    private static final KeycloakContainer KEYCLOAK_CONTAINER;

    private static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3.0"));

    static {
        REPOSITORY_CONTAINER.start();

        KEYCLOAK_CONTAINER = new KeycloakContainer(DockerImageName.parse("quay.io/keycloak/keycloak:22.0.4")
                        .asCanonicalNameString())
                .withRealmImportFile("keycloak/stratospheric-realm.json");
        KEYCLOAK_CONTAINER.start();

        LOCAL_STACK_CONTAINER.withServices(SQS);
        LOCAL_STACK_CONTAINER.start();

        try {
            LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal", "sqs", "create-queue", "--queue-name", "bls-book-sharing");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    private static void propertySource(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.client.registration.cognito.logoutUrl", KEYCLOAK_CONTAINER::getAuthServerUrl);
        registry.add(
                "spring.security.oauth2.client.provider.cognito.issuerUri",
                () -> KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/stratospheric");

        registry.add("spring.cloud.aws.sqs.endpoint", () -> LOCAL_STACK_CONTAINER.getEndpointOverride(SQS));
        registry.add("spring.cloud.aws.region.static", LOCAL_STACK_CONTAINER::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
    }
}
