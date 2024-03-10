package com.book.library.cdk.stack;

import com.book.library.cdk.construct.*;
import java.util.Map;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.cloudwatch.*;
import software.amazon.awscdk.services.cloudwatch.actions.SnsAction;
import software.amazon.awscdk.services.logs.FilterPattern;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.MetricFilter;
import software.amazon.awscdk.services.logs.MetricFilterProps;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.TopicProps;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;
import software.constructs.Construct;

public class MonitoringStack extends Stack {

    public MonitoringStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment,
            final String confirmationEmail) {
        super(scope, id, StackProps.builder().stackName(id).env(awsEnvironment).build());

        CognitoStack.CognitoOutputParameters cognitoOutputParameters =
                CognitoStack.getOutputParametersFromParameterStore(this, appEnvironment);

        Network.NetworkOutputParameters networkOutputParameters =
                Network.getOutputParametersFromParameterStore(this, appEnvironment);

        PostgresDatabase.DatabaseOutputParameters databaseOutputParameters =
                PostgresDatabase.getOutputParametersFromParameterStore(this, appEnvironment);

        String loadBalancerName = Fn.split(":loadbalancer/", networkOutputParameters.getLoadBalancerArn(), 2)
                .getFirst();

        new BasicCloudWatchDashboard(
                this,
                "basicCloudWatchDashboard",
                appEnvironment,
                awsEnvironment,
                new BasicCloudWatchDashboard.InputParameter(
                        cognitoOutputParameters.userPoolClientId(), cognitoOutputParameters.userPoolId()));

        new OperationalCloudWatchDashboard(
                this,
                "operationalCloudWatchDashboard",
                appEnvironment,
                awsEnvironment,
                new OperationalCloudWatchDashboard.InputParameter(
                        databaseOutputParameters.instanceId(), loadBalancerName));

        Alarm elbSlowResponseTimeAlarm = new Alarm(
                this,
                "elbSlowResponseTimeAlarm",
                AlarmProps.builder()
                        .alarmName("slow-api-response-alarm")
                        .alarmDescription("Indicating potential problems with the Spring Boot Backend")
                        .metric(new Metric(MetricProps.builder()
                                .namespace("AWS/ApplicationELB")
                                .metricName("TargetResponseTime")
                                .dimensionsMap(Map.of("LoadBalancer", loadBalancerName))
                                .region(awsEnvironment.getRegion())
                                .period(Duration.minutes(5))
                                .statistic("avg")
                                .build()))
                        .treatMissingData(TreatMissingData.NOT_BREACHING)
                        .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                        .evaluationPeriods(3)
                        .threshold(2)
                        .actionsEnabled(true)
                        .build());

        Alarm elb5xxAlarm = new Alarm(
                this,
                "elb5xxAlarm",
                AlarmProps.builder()
                        .alarmName("5xx-backend-alarm")
                        .alarmDescription(
                                "Alert on multiple HTTP 5xx ELB responses."
                                        + "See the runbook for a diagnosis and mitigation hints: https://github.com/BranislavBeno/Book-Library-Service/blob/master/docs/runbooks/elb5xxAlarm.md")
                        .metric(new Metric(MetricProps.builder()
                                .namespace("AWS/ApplicationELB")
                                .metricName("HTTPCode_ELB_5XX_Count")
                                .dimensionsMap(Map.of("LoadBalancer", loadBalancerName))
                                .region(awsEnvironment.getRegion())
                                .period(Duration.minutes(5))
                                .statistic("sum")
                                .build()))
                        .treatMissingData(TreatMissingData.NOT_BREACHING)
                        .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                        .evaluationPeriods(3)
                        .datapointsToAlarm(3)
                        .threshold(5)
                        .actionsEnabled(false)
                        .build());

        MetricFilter errorLogsMetricFilter = new MetricFilter(
                this,
                "errorLogsMetricFilter",
                MetricFilterProps.builder()
                        .metricName("backend-error-logs")
                        .metricNamespace("b-l-s")
                        .metricValue("1")
                        .defaultValue(0)
                        .logGroup(LogGroup.fromLogGroupName(this, "applicationLogGroup", appEnvironment + "-logs"))
                        .filterPattern(FilterPattern.stringValue("$.level", "=", "ERROR")) // { $.level = "ERROR" }
                        .build());

        Metric errorLogsMetric = errorLogsMetricFilter.metric(MetricOptions.builder()
                .period(Duration.minutes(5))
                .statistic("sum")
                .region(awsEnvironment.getRegion())
                .build());

        Alarm errorLogsAlarm = errorLogsMetric.createAlarm(
                this,
                "errorLogsAlarm",
                CreateAlarmOptions.builder()
                        .alarmName("backend-error-logs-alarm")
                        .alarmDescription("Alert on multiple ERROR backend logs")
                        .treatMissingData(TreatMissingData.NOT_BREACHING)
                        .comparisonOperator(ComparisonOperator.GREATER_THAN_OR_EQUAL_TO_THRESHOLD)
                        .evaluationPeriods(3)
                        .threshold(5)
                        .actionsEnabled(false)
                        .build());

        CompositeAlarm compositeAlarm = new CompositeAlarm(
                this,
                "basicCompositeAlarm",
                CompositeAlarmProps.builder()
                        .actionsEnabled(true)
                        .compositeAlarmName("backend-api-failure")
                        .alarmDescription("Showcasing a Composite Alarm")
                        .alarmRule(AlarmRule.allOf(
                                AlarmRule.fromAlarm(elb5xxAlarm, AlarmState.ALARM),
                                AlarmRule.fromAlarm(errorLogsAlarm, AlarmState.ALARM)))
                        .build());

        Topic snsAlarmingTopic = new Topic(
                this,
                "snsAlarmingTopic",
                TopicProps.builder()
                        .topicName(appEnvironment + "-alarming-topic")
                        .displayName("SNS Topic to further route Amazon CloudWatch Alarms")
                        .build());

        snsAlarmingTopic.addSubscription(
                EmailSubscription.Builder.create(confirmationEmail).build());

        elbSlowResponseTimeAlarm.addAlarmAction(new SnsAction(snsAlarmingTopic));
        compositeAlarm.addAlarmAction(new SnsAction(snsAlarmingTopic));
    }
}
