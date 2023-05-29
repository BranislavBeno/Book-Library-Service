package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.Network;
import com.book.library.cdk.construct.Service;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;

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
        var parametersStack = new Stack(
                app,
                "ServiceParameters-" + timestamp,
                StackProps.builder()
                        .stackName(appEnvironment.prefix("Service-Parameters-" + timestamp))
                        .env(awsEnvironment)
                        .build());

        String stackName =
                "%s-service-%s".formatted(appEnvironment.applicationName(), appEnvironment.environmentName());
        var serviceStack = new Stack(
                app,
                "ServiceStack",
                StackProps.builder().stackName(stackName).env(awsEnvironment).build());

        var dockerImageSource = new Service.DockerImageSource(dockerRepositoryName, dockerImageTag);
        var cognitoOutputParameters =
                CognitoStack.getOutputParametersFromParameterStore(parametersStack, appEnvironment);
        var serviceInputParameters = new Service.ServiceInputParameters(
                        dockerImageSource, environmentVariables(springProfile, cognitoOutputParameters))
                .withHealthCheckPath("/actuator/info")
                .withHealthCheckIntervalSeconds(30)
                .withTaskRolePolicyStatements(List.of(PolicyStatement.Builder.create()
                        .sid("AllowCreatingUsers")
                        .effect(Effect.ALLOW)
                        .resources(List.of(String.format(
                                "arn:aws:cognito-idp:%s:%s:userpool/%s",
                                region, accountId, cognitoOutputParameters.userPoolId())))
                        .actions(List.of("cognito-idp:AdminCreateUser"))
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

    static Map<String, String> environmentVariables(
            String springProfile, CognitoStack.CognitoOutputParameters cognitoOutputParameters) {
        Map<String, String> vars = new HashMap<>();
        vars.put("SPRING_PROFILES_ACTIVE", springProfile);
        vars.put("COGNITO_CLIENT_ID", cognitoOutputParameters.userPoolClientId());
        vars.put("COGNITO_CLIENT_SECRET", cognitoOutputParameters.userPoolClientSecret());
        vars.put("COGNITO_USER_POOL_ID", cognitoOutputParameters.userPoolId());
        vars.put("COGNITO_LOGOUT_URL", cognitoOutputParameters.logoutUrl());
        vars.put("COGNITO_PROVIDER_URL", cognitoOutputParameters.providerUrl());

        return vars;
    }
}
