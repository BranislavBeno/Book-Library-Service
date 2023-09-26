package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.PostgresDatabase;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class DatabaseApp {

    public static void main(final String[] args) {
        App app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName = CdkUtil.createStackName("database", appEnvironment);
        Stack databaseStack = new Stack(
                app,
                "DatabaseStack",
                StackProps.builder().stackName(stackName).env(awsEnvironment).build());

        new PostgresDatabase(
                databaseStack,
                "Database",
                appEnvironment,
                new PostgresDatabase.DatabaseInputParameters().withDockerImageVersion("postgres:15.4"));

        app.synth();
    }
}
