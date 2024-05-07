package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.stack.CanaryStack;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import java.io.IOException;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

public class CanaryApp {
    public static void main(final String[] args) throws IOException {
        App app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");
        String applicationUrl = Validations.requireNonEmpty(app, "applicationUrl");
        String canaryUsername = Validations.requireNonEmpty(app, "canaryUsername");
        String canaryUserPassword = Validations.requireNonEmpty(app, "canaryUserPassword");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);
        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName = CdkUtil.createStackName("canary", appEnvironment);
        new CanaryStack(
                app, stackName, awsEnvironment, appEnvironment, applicationUrl, canaryUsername, canaryUserPassword);

        app.synth();
    }
}
