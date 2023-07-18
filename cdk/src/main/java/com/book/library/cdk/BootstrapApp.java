package com.book.library.cdk;

import com.book.library.cdk.util.CdkUtil;
import com.book.library.cdk.util.Validations;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class BootstrapApp {

    public static void main(final String[] args) {
        var app = new App();

        String accountId = Validations.requireNonEmpty(app, "accountId");
        String region = Validations.requireNonEmpty(app, "region");

        Environment awsEnvironment = CdkUtil.makeEnv(accountId, region);

        new Stack(app, "Bootstrap", StackProps.builder().env(awsEnvironment).build());

        app.synth();
    }
}
