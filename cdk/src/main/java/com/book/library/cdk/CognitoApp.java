package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

class CognitoApp {
    public static void main(final String[] args) {
        App app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");
        String applicationUrl = Validations.requireNonEmpty(app, "applicationUrl");
        String loginPageDomainPrefix = Validations.requireNonEmpty(app, "loginPageDomainPrefix");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);
        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);
        var inputParameters =
                new CognitoStack.CognitoInputParameters(applicationName, applicationUrl, loginPageDomainPrefix);

        new CognitoStack(app, "Cognito", awsEnvironment, appEnvironment, inputParameters);

        app.synth();
    }
}
