package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.Network;
import com.book.library.cdk.construct.PostgresDatabase;
import com.book.library.cdk.construct.Service;
import com.book.library.cdk.stack.CognitoStack;
import com.book.library.cdk.stack.MessagingStack;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import java.util.*;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.constructs.Construct;

public class ServiceApp {

    public static void main(final String[] args) {
        var app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");
        String dockerRepositoryName = Validations.requireNonEmpty(app, "dockerRepositoryName");
        String dockerImageTag = Validations.requireNonEmpty(app, "dockerImageTag");
        String springProfile = Validations.requireNonEmpty(app, "springProfile");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        long timestamp = System.currentTimeMillis();
        String paramsStackName = CdkUtil.createStackName("service-params-" + timestamp, appEnvironment);
        var parametersStack = new Stack(
                app,
                "ServiceParameters-" + timestamp,
                StackProps.builder()
                        .stackName(paramsStackName)
                        .env(awsEnvironment)
                        .build());

        String serviceStackName = CdkUtil.createStackName("service", appEnvironment);
        var serviceStack = new Stack(
                app,
                "ServiceStack",
                StackProps.builder()
                        .stackName(serviceStackName)
                        .env(awsEnvironment)
                        .build());

        var dockerImageSource = new Service.DockerImageSource(dockerRepositoryName, dockerImageTag);

        var databaseOutputParameters =
                PostgresDatabase.getOutputParametersFromParameterStore(parametersStack, appEnvironment);

        var cognitoOutputParameters =
                CognitoStack.getOutputParametersFromParameterStore(parametersStack, appEnvironment);

        var messagingOutputParameters =
                MessagingStack.getOutputParametersFromParameterStore(parametersStack, appEnvironment);

        var serviceInputParameters = new Service.ServiceInputParameters(
                        dockerImageSource,
                        Collections.singletonList(databaseOutputParameters.databaseSecurityGroupId()),
                        environmentVariables(
                                serviceStack,
                                databaseOutputParameters,
                                cognitoOutputParameters,
                                messagingOutputParameters,
                                springProfile))
                .withHealthCheckPath("/actuator/info")
                .withHealthCheckIntervalSeconds(30)
                .withStickySessionsEnabled(true)
                .withTaskRolePolicyStatements(List.of(
                        PolicyStatement.Builder.create()
                                .sid("AllowCreatingUsers")
                                .effect(Effect.ALLOW)
                                .resources(List.of("arn:aws:cognito-idp:%s:%s:userpool/%s"
                                        .formatted(region, accountId, cognitoOutputParameters.userPoolId())))
                                .actions(List.of("cognito-idp:AdminCreateUser"))
                                .build(),
                        PolicyStatement.Builder.create()
                                .sid("AllowSQSAccess")
                                .effect(Effect.ALLOW)
                                .resources(List.of("arn:aws:sqs:%s:%s:%s"
                                        .formatted(region, accountId, messagingOutputParameters.sharingQueueName())))
                                .actions(Arrays.asList(
                                        "sqs:DeleteMessage",
                                        "sqs:GetQueueUrl",
                                        "sqs:ListDeadLetterSourceQueues",
                                        "sqs:ListQueues",
                                        "sqs:ListQueueTags",
                                        "sqs:ReceiveMessage",
                                        "sqs:SendMessage",
                                        "sqs:ChangeMessageVisibility",
                                        "sqs:GetQueueAttributes"))
                                .build()));

        var networkOutputParameters = Network.getOutputParametersFromParameterStore(serviceStack, appEnvironment);

        new Service(
                serviceStack,
                "Service",
                awsEnvironment,
                appEnvironment,
                serviceInputParameters,
                networkOutputParameters);

        app.synth();
    }

    private static Map<String, String> environmentVariables(
            Construct scope,
            PostgresDatabase.DatabaseOutputParameters databaseOutputParameters,
            CognitoStack.CognitoOutputParameters cognitoOutputParameters,
            MessagingStack.MessagingOutputParameters messagingOutputParameters,
            String springProfile) {
        Map<String, String> vars = new HashMap<>();

        String databaseSecretArn = databaseOutputParameters.databaseSecretArn();
        ISecret databaseSecret = Secret.fromSecretCompleteArn(scope, "databaseSecret", databaseSecretArn);

        vars.put("SPRING_PROFILES_ACTIVE", springProfile);
        vars.put(
                "SPRING_DATASOURCE_URL",
                "jdbc:postgresql://%s:%s/%s"
                        .formatted(
                                databaseOutputParameters.endpointAddress(),
                                databaseOutputParameters.endpointPort(),
                                databaseOutputParameters.dbName()));
        vars.put(
                "SPRING_DATASOURCE_USERNAME",
                databaseSecret.secretValueFromJson("username").unsafeUnwrap());
        vars.put(
                "SPRING_DATASOURCE_PASSWORD",
                databaseSecret.secretValueFromJson("password").unsafeUnwrap());
        vars.put("COGNITO_CLIENT_ID", cognitoOutputParameters.userPoolClientId());
        vars.put("COGNITO_CLIENT_SECRET", cognitoOutputParameters.userPoolClientSecret());
        vars.put("COGNITO_USER_POOL_ID", cognitoOutputParameters.userPoolId());
        vars.put("COGNITO_LOGOUT_URL", cognitoOutputParameters.logoutUrl());
        vars.put("COGNITO_PROVIDER_URL", cognitoOutputParameters.providerUrl());
        vars.put("BLS_SHARING_QUEUE_NAME", messagingOutputParameters.sharingQueueName());

        return vars;
    }
}
