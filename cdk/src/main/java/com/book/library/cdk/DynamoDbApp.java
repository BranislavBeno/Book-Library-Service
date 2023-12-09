package com.book.library.cdk;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.BreadcrumbsDynamoDbTable;
import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class DynamoDbApp {

    public static void main(String[] args) {
        var app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");
        String environmentName = Validations.requireNonEmpty(app, "environmentName");
        String applicationName = Validations.requireNonEmpty(app, "applicationName");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        var appEnvironment = new ApplicationEnvironment(applicationName, environmentName);

        String stackName = CdkUtil.createStackName("dynamo-db", appEnvironment);
        var dynamoDbStack = new Stack(
                app,
                "DynamoDbStack",
                StackProps.builder().stackName(stackName).env(awsEnvironment).build());

        new BreadcrumbsDynamoDbTable(
                dynamoDbStack,
                "BreadcrumbTable",
                appEnvironment,
                new BreadcrumbsDynamoDbTable.InputParameter("breadcrumb"));

        app.synth();
    }
}
