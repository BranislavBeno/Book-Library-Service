package com.book.library.cdk.construct;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.services.cloudwatch.*;
import software.constructs.Construct;

public class OperationalCloudWatchDashboard extends Construct {

    private static final String METRIC_NAMESPACE = "b-l-s";
    private static final String AWS_RDS_NAMESPACE = "AWS/RDS";
    private static final String DB_INSTANCE_IDENTIFIER = "DBInstanceIdentifier";
    private static final String BOOK_RECOMMENDATION_QUEUE = "book-recommendation-queue";
    private static final String BOOK_RECOMMENDATION_DEAD_LETTER_QUEUE = "book-recommendation-dead-letter-queue";
    private static final String ENVIRONMENT_NAME = "environment";
    private static final String JVM_MEMORY_USED_VALUE = "jvm.memory.used.value";
    private static final String JVM_MEMORY_COMMITTED_VALUE = "jvm.memory.committed.value";

    public OperationalCloudWatchDashboard(
            @NotNull Construct scope,
            @NotNull String id,
            ApplicationEnvironment appEnvironment,
            Environment awsEnvironment,
            InputParameter inputParameter) {
        super(scope, id);

        new Dashboard(
                this,
                "operationalApplicationDashboard",
                DashboardProps.builder()
                        .dashboardName(appEnvironment.prefix("operational-application-dashboard"))
                        .widgets(List.of(
                                List.of(
                                        TextWidget.Builder.create()
                                                .markdown(
                                                        """
                                                # Operations Dashboard
                                                Created with the AWS CDK.
                                                * IaC
                                                * Configurable
                                                * Nice-looking
                                                """)
                                                .height(6)
                                                .width(6)
                                                .build(),
                                        LogQueryWidget.Builder.create()
                                                .title("Application Logs")
                                                .logGroupNames(List.of(appEnvironment + "-logs"))
                                                .queryString(
                                                        """
                                                fields timestamp, message, logger, @logStream
                                                | sort timestamp desc
                                                | limit 100""")
                                                .height(6)
                                                .width(18)
                                                .build()),
                                List.of(
                                        GraphWidget.Builder.create()
                                                .title("Count Per Log Level")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .left(List.of(
                                                        createLogMetric(appEnvironment, awsEnvironment, "error"),
                                                        createLogMetric(appEnvironment, awsEnvironment, "warn"),
                                                        createLogMetric(appEnvironment, awsEnvironment, "info")))
                                                .height(6)
                                                .width(12)
                                                .build(),
                                        LogQueryWidget.Builder.create()
                                                .title("Application Error Logs")
                                                .logGroupNames(List.of(appEnvironment + "-logs"))
                                                .queryString(
                                                        """
                                                fields message, logger, @logStream
                                                | filter (level = 'ERROR' OR level = 'WARN')
                                                | sort timestamp desc""")
                                                .height(6)
                                                .width(12)
                                                .build()),
                                List.of(
                                        GraphWidget.Builder.create()
                                                .title("CPU Usage in %")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        new MathExpression(
                                                                MathExpressionProps.builder()
                                                                        .label("JVM Process CPU Usage")
                                                                        .expression("100*(processCpu)")
                                                                        .usingMetrics(
                                                                                Map.of(
                                                                                        "processCpu",
                                                                                        new Metric(
                                                                                                MetricProps.builder()
                                                                                                        .namespace(
                                                                                                                METRIC_NAMESPACE)
                                                                                                        .metricName(
                                                                                                                "process.cpu.usage.value")
                                                                                                        .period(
                                                                                                                Duration
                                                                                                                        .minutes(
                                                                                                                                5))
                                                                                                        .dimensionsMap(
                                                                                                                Map.of(
                                                                                                                        ENVIRONMENT_NAME,
                                                                                                                        appEnvironment
                                                                                                                                .environmentName()))
                                                                                                        .statistic(
                                                                                                                "avg")
                                                                                                        .build())))
                                                                        .build()),
                                                        new MathExpression(
                                                                MathExpressionProps.builder()
                                                                        .label("System CPU Usage")
                                                                        .expression("100*(systemCpu)")
                                                                        .usingMetrics(
                                                                                Map.of(
                                                                                        "systemCpu",
                                                                                        new Metric(
                                                                                                MetricProps.builder()
                                                                                                        .namespace(
                                                                                                                METRIC_NAMESPACE)
                                                                                                        .metricName(
                                                                                                                "system.cpu.usage.value")
                                                                                                        .period(
                                                                                                                Duration
                                                                                                                        .minutes(
                                                                                                                                5))
                                                                                                        .dimensionsMap(
                                                                                                                Map.of(
                                                                                                                        ENVIRONMENT_NAME,
                                                                                                                        appEnvironment
                                                                                                                                .environmentName()))
                                                                                                        .statistic(
                                                                                                                "avg")
                                                                                                        .build())))
                                                                        .build())))
                                                .height(6)
                                                .width(12)
                                                .build(),
                                        GraphWidget.Builder.create()
                                                .title("JVM Memory Overview")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        new MathExpression(
                                                                MathExpressionProps.builder()
                                                                        .label("JVM Heap Memory Used")
                                                                        .expression(
                                                                                "(survivorUsed + edenUsed + tenuredUsed)/1000000")
                                                                        .usingMetrics(
                                                                                Map.of(
                                                                                        "survivorUsed",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Survivor Space",
                                                                                                        JVM_MEMORY_USED_VALUE),
                                                                                        "edenUsed",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Eden Space",
                                                                                                        JVM_MEMORY_USED_VALUE),
                                                                                        "tenuredUsed",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Tenured Gen",
                                                                                                        JVM_MEMORY_USED_VALUE)))
                                                                        .build()),
                                                        new MathExpression(
                                                                MathExpressionProps.builder()
                                                                        .label("JVM Heap Memory Committed")
                                                                        .expression(
                                                                                "(survivorCommitted + edenCommitted + tenuredCommitted)/1000000")
                                                                        .usingMetrics(
                                                                                Map.of(
                                                                                        "survivorCommitted",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Survivor Space",
                                                                                                        JVM_MEMORY_COMMITTED_VALUE),
                                                                                        "edenCommitted",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Eden Space",
                                                                                                        JVM_MEMORY_COMMITTED_VALUE),
                                                                                        "tenuredCommitted",
                                                                                                createJvmMemoryMetric(
                                                                                                        appEnvironment,
                                                                                                        "Tenured Gen",
                                                                                                        JVM_MEMORY_COMMITTED_VALUE)))
                                                                        .build())))
                                                .height(6)
                                                .width(12)
                                                .build()),
                                List.of(
                                        GraphWidget.Builder.create()
                                                .title("ELB Target Response Codes")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        createElbTargetResponseCountMetric(
                                                                "HTTPCode_Target_2XX_Count",
                                                                inputParameter.beanstalkLoadBalancerId),
                                                        createElbTargetResponseCountMetric(
                                                                "HTTPCode_Target_3XX_Count",
                                                                inputParameter.beanstalkLoadBalancerId),
                                                        createElbTargetResponseCountMetric(
                                                                "HTTPCode_Target_4XX_Count",
                                                                inputParameter.beanstalkLoadBalancerId),
                                                        createElbTargetResponseCountMetric(
                                                                "HTTPCode_Target_5XX_Count",
                                                                inputParameter.beanstalkLoadBalancerId)))
                                                .height(6)
                                                .width(12)
                                                .build(),
                                        GraphWidget.Builder.create()
                                                .title("ELB Avg. Response Times")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        createElbTargetResponseTimeMetric(
                                                                "avg", inputParameter.beanstalkLoadBalancerId),
                                                        createElbTargetResponseTimeMetric(
                                                                "max", inputParameter.beanstalkLoadBalancerId),
                                                        createElbTargetResponseTimeMetric(
                                                                "min", inputParameter.beanstalkLoadBalancerId)))
                                                .height(6)
                                                .width(12)
                                                .build()),
                                List.of(
                                        GraphWidget.Builder.create()
                                                .title("RDS Open Connections")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(new Metric(MetricProps.builder()
                                                        .namespace(AWS_RDS_NAMESPACE)
                                                        .metricName("DatabaseConnections")
                                                        .dimensionsMap(Map.of(
                                                                DB_INSTANCE_IDENTIFIER,
                                                                inputParameter.rdsDatabaseIdentifier))
                                                        .period(Duration.minutes(1))
                                                        .statistic("sum")
                                                        .build())))
                                                .height(6)
                                                .width(12)
                                                .build(),
                                        GraphWidget.Builder.create()
                                                .title("RDS CPU/Storage")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(new Metric(MetricProps.builder()
                                                        .namespace(AWS_RDS_NAMESPACE)
                                                        .metricName("CPUUtilization")
                                                        .dimensionsMap(Map.of(
                                                                DB_INSTANCE_IDENTIFIER,
                                                                inputParameter.rdsDatabaseIdentifier))
                                                        .period(Duration.minutes(1))
                                                        .statistic("avg")
                                                        .build())))
                                                .right(List.of(new Metric(MetricProps.builder()
                                                        .namespace(AWS_RDS_NAMESPACE)
                                                        .metricName("FreeStorageSpace")
                                                        .dimensionsMap(Map.of(
                                                                DB_INSTANCE_IDENTIFIER,
                                                                inputParameter.rdsDatabaseIdentifier))
                                                        .period(Duration.minutes(1))
                                                        .statistic("sum")
                                                        .build())))
                                                .height(6)
                                                .width(12)
                                                .build()),
                                List.of(
                                        GraphWidget.Builder.create()
                                                .title("SQS Queue Metrics")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        createSqsMetric(
                                                                "ApproximateAgeOfOldestMessage",
                                                                appEnvironment.prefix(BOOK_RECOMMENDATION_QUEUE),
                                                                "avg"),
                                                        createSqsMetric(
                                                                "ApproximateNumberOfMessagesVisible",
                                                                appEnvironment.prefix(BOOK_RECOMMENDATION_QUEUE),
                                                                "sum"),
                                                        createSqsMetric(
                                                                "NumberOfMessagesSent",
                                                                appEnvironment.prefix(BOOK_RECOMMENDATION_QUEUE),
                                                                "sum")))
                                                .height(6)
                                                .width(12)
                                                .build(),
                                        GraphWidget.Builder.create()
                                                .title("SQS DLQ Metrics")
                                                .view(GraphWidgetView.TIME_SERIES)
                                                .region(awsEnvironment.getRegion())
                                                .setPeriodToTimeRange(true)
                                                .left(List.of(
                                                        createSqsMetric(
                                                                "ApproximateAgeOfOldestMessage",
                                                                appEnvironment.prefix(
                                                                        BOOK_RECOMMENDATION_DEAD_LETTER_QUEUE),
                                                                "avg"),
                                                        createSqsMetric(
                                                                "ApproximateNumberOfMessagesVisible",
                                                                appEnvironment.prefix(
                                                                        BOOK_RECOMMENDATION_DEAD_LETTER_QUEUE),
                                                                "sum"),
                                                        createSqsMetric(
                                                                "NumberOfMessagesSent",
                                                                appEnvironment.prefix(
                                                                        BOOK_RECOMMENDATION_DEAD_LETTER_QUEUE),
                                                                "sum")))
                                                .height(6)
                                                .width(12)
                                                .build())))
                        .build());
    }

    @NotNull
    private Metric createSqsMetric(String metricName, String queueName, String statistic) {
        return new Metric(MetricProps.builder()
                .namespace("AWS/SQS")
                .metricName(metricName)
                .dimensionsMap(Map.of("QueueName", queueName))
                .period(Duration.minutes(1))
                .statistic(statistic)
                .build());
    }

    @NotNull
    private Metric createElbTargetResponseTimeMetric(String statistic, String loadBalancerId) {
        return new Metric(MetricProps.builder()
                .namespace("AWS/ApplicationELB")
                .metricName("TargetResponseTime")
                .dimensionsMap(Map.of("LoadBalancer", loadBalancerId))
                .period(Duration.minutes(1))
                .statistic(statistic)
                .build());
    }

    @NotNull
    private IMetric createElbTargetResponseCountMetric(String metricName, String loadBalancerId) {
        return new Metric(MetricProps.builder()
                .namespace("AWS/ApplicationELB")
                .metricName(metricName)
                .dimensionsMap(Map.of("LoadBalancer", loadBalancerId))
                .period(Duration.minutes(15))
                .statistic("sum")
                .build());
    }

    @NotNull
    private IMetric createJvmMemoryMetric(ApplicationEnvironment appEnvironment, String id, String metricName) {
        return new Metric(MetricProps.builder()
                .namespace(METRIC_NAMESPACE)
                .metricName(metricName)
                .dimensionsMap(Map.of(ENVIRONMENT_NAME, appEnvironment.environmentName(), "area", "heap", "id", id))
                .period(Duration.minutes(1))
                .statistic("avg")
                .build());
    }

    @NotNull
    private IMetric createLogMetric(
            ApplicationEnvironment appEnvironment, Environment awsEnvironment, String logLevel) {
        return new Metric(MetricProps.builder()
                .namespace(METRIC_NAMESPACE)
                .region(awsEnvironment.getRegion())
                .metricName("logback.events.count")
                .period(Duration.minutes(5))
                .dimensionsMap(Map.of(ENVIRONMENT_NAME, appEnvironment.environmentName(), "level", logLevel))
                .statistic("sum")
                .build());
    }

    public record InputParameter(String rdsDatabaseIdentifier, String beanstalkLoadBalancerId) {}
}
