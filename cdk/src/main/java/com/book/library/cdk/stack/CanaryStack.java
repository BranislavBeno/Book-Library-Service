package com.book.library.cdk.stack;

import static java.util.Collections.singletonList;

import com.book.library.cdk.construct.ApplicationEnvironment;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.cloudwatch.*;
import software.amazon.awscdk.services.iam.*;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.synthetics.CfnCanary;
import software.constructs.Construct;

public class CanaryStack extends Stack {

    public static final String CANARY_SCRIPT_PATH = "canaries/create-book-canary.js";

    public CanaryStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment,
            final String applicationUrl,
            final String username,
            final String password)
            throws IOException {
        super(
                scope,
                id,
                StackProps.builder()
                        .stackName(appEnvironment.prefix("Canary"))
                        .env(awsEnvironment)
                        .build());

        Bucket bucket = Bucket.Builder.create(this, "canaryBucket")
                .bucketName(appEnvironment.prefix("canary-bucket"))
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();

        Role executionRole = Role.Builder.create(this, "canaryExecutionRole")
                .roleName(appEnvironment.prefix("canary-execution-role"))
                .assumedBy(new AnyPrincipal())
                .inlinePolicies(Map.of(
                        appEnvironment.prefix("canaryExecutionRolePolicy"),
                        PolicyDocument.Builder.create()
                                .statements(singletonList(PolicyStatement.Builder.create()
                                        .effect(Effect.ALLOW)
                                        .resources(singletonList("*"))
                                        .actions(Arrays.asList(
                                                "s3:PutObject",
                                                "s3:GetBucketLocation",
                                                "s3:ListAllMyBuckets",
                                                "cloudwatch:PutMetricData",
                                                "logs:CreateLogGroup",
                                                "logs:CreateLogStream",
                                                "logs:PutLogEvents"))
                                        .build()))
                                .build()))
                .build();

        String canaryName = appEnvironment.prefix("canary", 21);

        CfnCanary.Builder.create(this, "canary")
                .name(canaryName)
                .runtimeVersion("syn-nodejs-puppeteer-7.0")
                .artifactS3Location(bucket.s3UrlForObject("create-todo-canary"))
                .startCanaryAfterCreation(Boolean.TRUE)
                .executionRoleArn(executionRole.getRoleArn())
                .schedule(CfnCanary.ScheduleProperty.builder()
                        .expression("rate(15 minutes)")
                        .build())
                .runConfig(CfnCanary.RunConfigProperty.builder()
                        .environmentVariables(Map.of(
                                "TARGET_URL", applicationUrl,
                                "USER_NAME", username,
                                "PASSWORD", password))
                        .timeoutInSeconds(30)
                        .build())
                .code(CfnCanary.CodeProperty.builder()
                        .handler("recordedScript.handler")
                        .script(getScriptFromResource())
                        .build())
                .build();

        new Alarm(
                this,
                "canaryAlarm",
                AlarmProps.builder()
                        .alarmName("canary-failed-alarm")
                        .alarmDescription("Alert on multiple Canary failures")
                        .metric(new Metric(MetricProps.builder()
                                .namespace("CloudWatchSynthetics")
                                .metricName("Failed")
                                .dimensionsMap(Map.of("CanaryName", canaryName))
                                .region(awsEnvironment.getRegion())
                                .period(Duration.minutes(50))
                                .statistic("sum")
                                .build()))
                        .treatMissingData(TreatMissingData.NOT_BREACHING)
                        .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                        .evaluationPeriods(1)
                        .threshold(3)
                        .actionsEnabled(false)
                        .build());
    }

    private String getScriptFromResource() throws IOException {
        Path path = Path.of(CANARY_SCRIPT_PATH);
        try (Scanner scanner = new Scanner(path)) {
            var script = new StringBuilder();
            while (scanner.hasNextLine()) {
                script.append(scanner.nextLine());
                script.append("\n");
            }

            return script.toString();
        }
    }
}
