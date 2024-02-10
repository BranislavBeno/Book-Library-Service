package com.book.library.cdk.construct;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.services.cloudwatch.*;
import software.constructs.Construct;

public class BasicCloudWatchDashboard extends Construct {

    public BasicCloudWatchDashboard(
            @NotNull Construct scope,
            @NotNull String id,
            ApplicationEnvironment appEnvironment,
            Environment awsEnvironment,
            InputParameter inputParameter) {
        super(scope, id);

        new Dashboard(
                this,
                "basicApplicationDashboard",
                DashboardProps.builder()
                        .dashboardName(appEnvironment + "-basic-application-dashboard")
                        .widgets(List.of(List.of(
                                TextWidget.Builder.create()
                                        .markdown(
                                                """
                                                # Book Library Service Dashboard
                                                  Created with AWS CDK.
                                                  * IaC
                                                  * Configurable
                                                  * Nice-looking""")
                                        .height(6)
                                        .width(6)
                                        .build(),
                                SingleValueWidget.Builder.create()
                                        .title("User Registrations")
                                        .setPeriodToTimeRange(true)
                                        .metrics(List.of(new Metric(MetricProps.builder()
                                                .namespace("b-l-s")
                                                .metricName("b-l-s.registration.signups.count")
                                                .region(awsEnvironment.getRegion())
                                                .statistic("sum")
                                                .dimensionsMap(Map.of(
                                                        "outcome",
                                                        "success",
                                                        "environment",
                                                        appEnvironment.environmentName()))
                                                .build())))
                                        .height(6)
                                        .width(6)
                                        .build(),
                                GraphWidget.Builder.create()
                                        .title("User Sign In")
                                        .view(GraphWidgetView.BAR)
                                        .left(List.of(new Metric(MetricProps.builder()
                                                .namespace("AWS/Cognito")
                                                .metricName("SignInSuccesses")
                                                .period(Duration.minutes(15))
                                                .region(awsEnvironment.getRegion())
                                                .dimensionsMap(Map.of(
                                                        "UserPoolClient", inputParameter.cognitoUserPoolClientId(),
                                                        "UserPool", inputParameter.cognitoUserPoolId()))
                                                .statistic("sum")
                                                .build())))
                                        .right(List.of(new Metric(MetricProps.builder()
                                                .namespace("AWS/Cognito")
                                                .metricName("TokenRefreshSuccesses")
                                                .period(Duration.minutes(15))
                                                .region(awsEnvironment.getRegion())
                                                .dimensionsMap(Map.of(
                                                        "UserPoolClient", inputParameter.cognitoUserPoolClientId(),
                                                        "UserPool", inputParameter.cognitoUserPoolId()))
                                                .statistic("sum")
                                                .build())))
                                        .height(6)
                                        .width(6)
                                        .build(),
                                LogQueryWidget.Builder.create()
                                        .view(LogQueryVisualizationType.TABLE)
                                        .title("Backend Logs")
                                        .logGroupNames(List.of(appEnvironment + "-logs"))
                                        .queryString(
                                                """
                                                fields @timestamp, @message
                                                | sort @timestamp desc
                                                | limit 20
                                                | display timestamp, message""")
                                        .height(6)
                                        .width(6)
                                        .build())))
                        .build());
    }

    public record InputParameter(String cognitoUserPoolClientId, String cognitoUserPoolId) {}
}
