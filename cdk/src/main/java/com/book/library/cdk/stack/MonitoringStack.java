package com.book.library.cdk.stack;

import com.book.library.cdk.construct.*;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class MonitoringStack extends Stack {

    public MonitoringStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment) {
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
    }
}
