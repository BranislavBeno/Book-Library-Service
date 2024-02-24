package com.book.library.cdk.stack;

import com.book.library.cdk.construct.*;
import com.book.library.cdk.util.CdkUtil;
import java.util.Map;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.cloudwatch.*;
import software.amazon.awscdk.services.cloudwatch.actions.SnsAction;
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

        Topic snsAlarmingTopic = new Topic(
                this,
                "snsAlarmingTopic",
                TopicProps.builder()
                        .topicName(CdkUtil.createStackName("alarming-topic", appEnvironment))
                        .displayName("SNS Topic to further route Amazon CloudWatch Alarms")
                        .build());

        snsAlarmingTopic.addSubscription(
                EmailSubscription.Builder.create(confirmationEmail).build());

        elbSlowResponseTimeAlarm.addAlarmAction(new SnsAction(snsAlarmingTopic));
    }
}
