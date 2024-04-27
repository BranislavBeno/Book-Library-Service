package com.book.library.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import java.io.IOException;
import java.util.List;
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
        return new PostgreSQLContainer<>("postgres:16.2");
    }

    @Bean
    @RestartScope
    public KeycloakContainer keycloakContainer(DynamicPropertyRegistry registry) {
        try (var container = new KeycloakContainer(
                DockerImageName.parse("quay.io/keycloak/keycloak:24.0.3").asCanonicalNameString())) {
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
        try (var container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4.0"))) {
            container.withServices(
                    LocalStackContainer.Service.SQS,
                    LocalStackContainer.Service.SES,
                    LocalStackContainer.Service.DYNAMODB);
            container.start();

            Container.ExecResult createQueue = container.execInContainer(
                    "awslocal", "sqs", "create-queue", "--queue-name", "bls-book-recommendation");
            LOGGER.info("SQS queue creation finished with exit code {}.", createQueue.getExitCode());
            LOGGER.info(createQueue.getStdout());

            Container.ExecResult tracingTable = container.execInContainer(
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
            LOGGER.info("DynamoDB table creation finished with exit code {}.", tracingTable.getExitCode());
            LOGGER.info(tracingTable.getStdout());
            LOGGER.info(tracingTable.getStderr());

            List<String> emails = List.of(
                    "duke@b-l-s.click",
                    "mike@b-l-s.click",
                    "jan@b-l-s.click",
                    "lukas@b-l-s.click",
                    "info@b-l-s.click",
                    "noreply@b-l-s.click");
            for (String email : emails) {
                verifyEmail(container, email);
            }

            registry.add(
                    "spring.cloud.aws.sqs.endpoint",
                    () -> container.getEndpointOverride(LocalStackContainer.Service.SQS));
            registry.add(
                    "spring.cloud.aws.dynamodb.endpoint",
                    () -> container.getEndpointOverride(LocalStackContainer.Service.DYNAMODB));
            registry.add("spring.cloud.aws.region.static", container::getRegion);
            registry.add("spring.cloud.aws.credentials.access-key", container::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", container::getSecretKey);

            return container;
        }
    }

    private void verifyEmail(Container<?> container, String email) throws IOException, InterruptedException {
        Container.ExecResult verifyEmail =
                container.execInContainer("awslocal", "ses", "verify-email-identity", "--email-address", email);
        LOGGER.info("SES email verification of {} ended with exit code {}.", email, verifyEmail.getExitCode());
    }
}
