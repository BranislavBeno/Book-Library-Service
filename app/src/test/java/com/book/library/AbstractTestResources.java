package com.book.library;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractTestResources {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestResources.class);

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

        LOCAL_STACK_CONTAINER.withServices(LocalStackContainer.Service.SQS);
        LOCAL_STACK_CONTAINER.withServices(LocalStackContainer.Service.DYNAMODB);
        LOCAL_STACK_CONTAINER.start();

        try {
            Container.ExecResult createQueue = LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal", "sqs", "create-queue", "--queue-name", "testing-queue");
            LOG.info("SQS queue creation finished with exit code {}.", createQueue.getExitCode());
            LOG.info(createQueue.getStdout());

            Container.ExecResult createDynamoDb = LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal",
                    "dynamodb",
                    "create-table",
                    "--table-name",
                    "Notifications",
                    "--attribute-definitions",
                    "AttributeName=notificationId,AttributeType=S",
                    "--key-schema",
                    "AttributeName=notificationId,KeyType=HASH",
                    "--provisioned-throughput",
                    "ReadCapacityUnits=5,WriteCapacityUnits=5");
            LOG.info("DynamoDB creation finished with exit code {}.", createDynamoDb.getExitCode());
            LOG.info(createDynamoDb.getStdout());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.client.registration.cognito.logoutUrl", KEYCLOAK_CONTAINER::getAuthServerUrl);
        registry.add(
                "spring.security.oauth2.client.provider.cognito.issuerUri",
                () -> KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/stratospheric");

        registry.add(
                "spring.cloud.aws.sqs.endpoint",
                () -> LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.SQS));
        registry.add(
                "spring.cloud.aws.dynamodb.endpoint",
                () -> LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB));
        registry.add("spring.cloud.aws.region.static", LOCAL_STACK_CONTAINER::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
        registry.add("custom.recommendation-queue", () -> "testing-queue");
        registry.add("custom.use-real-sqs-listener", () -> "false");
    }
}
