package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.Network;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class NetworkApp {

    public static void main(final String[] args) {
        var app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName =
                "%s-network-%s".formatted(appEnvironment.applicationName(), appEnvironment.environmentName());
        var networkStack = new Stack(
                app,
                "NetworkStack",
                StackProps.builder().stackName(stackName).env(awsEnvironment).build());

        new Network(networkStack, "Network", appEnvironment, new Network.NetworkInputParameters());

        app.synth();
    }
}
