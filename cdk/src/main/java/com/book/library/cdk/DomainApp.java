package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.stack.DomainStack;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;

public class DomainApp {

    public static void main(String[] args) {
        App app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");
        String applicationDomain = Validations.requireNonEmpty(app, "applicationDomain");
        String hostedZoneDomain = Validations.requireNonEmpty(app, "hostedZoneDomain");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName = CdkUtil.createStackName("domain", appEnvironment);
        new DomainStack(app, stackName, awsEnvironment, appEnvironment, hostedZoneDomain, applicationDomain);

        app.synth();
    }
}
