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
        String sslCertificateArn = (String) app.getNode().tryGetContext("sslCertificateArn");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName = CdkUtil.createStackName("network", appEnvironment);
        var networkStack = new Stack(
                app,
                "NetworkStack",
                StackProps.builder().stackName(stackName).env(awsEnvironment).build());

        var inputParameters = sslCertificateArn.isEmpty()
                ? new Network.NetworkInputParameters()
                : new Network.NetworkInputParameters(sslCertificateArn);

        new Network(networkStack, "Network", appEnvironment, inputParameters);

        app.synth();
    }
}
