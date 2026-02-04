package com.book.library;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.locations=classpath:/db/migration/postgresql")
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractTestResources {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestResources.class);

    @ServiceConnection
    private static final PostgreSQLContainer REPOSITORY_CONTAINER =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18.1"));

    private static final KeycloakContainer KEYCLOAK_CONTAINER;

    private static final LocalStackContainer LOCAL_STACK_CONTAINER =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.12.0"));

    static {
        REPOSITORY_CONTAINER.start();

        KEYCLOAK_CONTAINER = new KeycloakContainer(DockerImageName.parse("quay.io/keycloak/keycloak:26.5.2")
                        .asCanonicalNameString())
                .withRealmImportFile("keycloak/stratospheric-realm.json");
        KEYCLOAK_CONTAINER.start();

        LOCAL_STACK_CONTAINER.withServices("sqs", "ses", "dynamodb");
        LOCAL_STACK_CONTAINER.start();

        try {
            Container.ExecResult createQueue = LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal", "sqs", "create-queue", "--queue-name", "testing-queue");
            LOG.info("SQS queue creation finished with exit code {}.", createQueue.getExitCode());
            LOG.info(createQueue.getStdout());

            Container.ExecResult notificationTable = LOCAL_STACK_CONTAINER.execInContainer(
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
            LOG.info(
                    "DynamoDB notification table creation finished with exit code {}.",
                    notificationTable.getExitCode());
            LOG.info(notificationTable.getStdout());

            Container.ExecResult tracingTable = LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal",
                    "dynamodb",
                    "create-table",
                    "--table-name",
                    "bls-local-breadcrumb",
                    "--attribute-definitions",
                    "AttributeName=id,AttributeType=S",
                    "--key-schema",
                    "AttributeName=id,KeyType=HASH",
                    "--provisioned-throughput",
                    "ReadCapacityUnits=10,WriteCapacityUnits=10");
            LOG.info("DynamoDB tracing table creation finished with exit code {}.", tracingTable.getExitCode());
            LOG.info(tracingTable.getStdout());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Stream.of(
                        "duke@b-l-s.click",
                        "mike@b-l-s.click",
                        "jan@b-l-s.click",
                        "lukas@b-l-s.click",
                        "info@b-l-s.click",
                        "noreply@b-l-s.click")
                .forEach(AbstractTestResources::verifyEmail);
    }

    private static void verifyEmail(String email) {
        try {
            Container.ExecResult verifyEmail = LOCAL_STACK_CONTAINER.execInContainer(
                    "awslocal", "ses", "verify-email-identity", "--email-address", email);
            LOG.info("SES email verification of {} ended with exit code {}.", email, verifyEmail.getExitCode());
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
                () -> LOCAL_STACK_CONTAINER.getEndpoint().toString());
        registry.add(
                "spring.cloud.aws.dynamodb.endpoint",
                () -> LOCAL_STACK_CONTAINER.getEndpoint().toString());
        registry.add("spring.cloud.aws.region.static", LOCAL_STACK_CONTAINER::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
        registry.add("custom.recommendation-queue", () -> "testing-queue");
        registry.add("custom.use-real-sqs-listener", () -> "false");
    }
}
