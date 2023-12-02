package com.book.library.cdk.stack;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.util.CdkUtil;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.IQueue;
import software.amazon.awscdk.services.sqs.Queue;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

public final class MessagingStack extends Stack {
    private final ApplicationEnvironment appEnvironment;
    private final IQueue recommendationQueue;

    public MessagingStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment) {
        super(
                scope,
                id,
                StackProps.builder()
                        .stackName(CdkUtil.createStackName("messaging", appEnvironment))
                        .env(awsEnvironment)
                        .build());

        this.appEnvironment = appEnvironment;

        IQueue recommendationDlq = Queue.Builder.create(this, "blsRecommendationDlq")
                .queueName(appEnvironment.prefix("bls-recommendation-dead-letter-queue"))
                .retentionPeriod(Duration.days(14))
                .build();

        this.recommendationQueue = Queue.Builder.create(this, "blsRecommendationQueue")
                .queueName(appEnvironment.prefix("bls-recommendation-queue"))
                .visibilityTimeout(Duration.seconds(30))
                .retentionPeriod(Duration.days(14))
                .deadLetterQueue(DeadLetterQueue.builder()
                        .queue(recommendationDlq)
                        .maxReceiveCount(3)
                        .build())
                .build();

        createOutputParameters();

        appEnvironment.tag(this);
    }

    private static final String PARAMETER_RECOMMENDATION_QUEUE_NAME = "blsRecommendationQueueName";

    private void createOutputParameters() {
        StringParameter.Builder.create(this, PARAMETER_RECOMMENDATION_QUEUE_NAME)
                .parameterName(createParameterName(appEnvironment))
                .stringValue(this.recommendationQueue.getQueueName())
                .build();
    }

    private static String createParameterName(ApplicationEnvironment applicationEnvironment) {
        return applicationEnvironment.environmentName() + "-" + applicationEnvironment.applicationName() + "-Messaging-"
                + MessagingStack.PARAMETER_RECOMMENDATION_QUEUE_NAME;
    }

    public static String getRecommendationQueueName(Construct scope, ApplicationEnvironment applicationEnvironment) {
        return StringParameter.fromStringParameterName(
                        scope, PARAMETER_RECOMMENDATION_QUEUE_NAME, createParameterName(applicationEnvironment))
                .getStringValue();
    }

    public static MessagingOutputParameters getOutputParametersFromParameterStore(
            Construct scope, ApplicationEnvironment applicationEnvironment) {
        return new MessagingOutputParameters(getRecommendationQueueName(scope, applicationEnvironment));
    }

    public record MessagingOutputParameters(String recommendationQueueName) {}
}
