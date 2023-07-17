package com.book.library.cdk.stack;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.construct.Network;
import com.book.library.cdk.util.CdkUtil;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancer;
import software.amazon.awscdk.services.elasticloadbalancingv2.ApplicationLoadBalancerAttributes;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancer;
import software.amazon.awscdk.services.route53.*;
import software.amazon.awscdk.services.route53.targets.LoadBalancerTarget;
import software.constructs.Construct;

public class DomainStack extends Stack {

    public DomainStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment,
            final String hostedZoneDomain,
            final String applicationDomain) {
        super(
                scope,
                id,
                StackProps.builder()
                        .stackName(CdkUtil.createStackName("domain", appEnvironment))
                        .env(awsEnvironment)
                        .build());

        IHostedZone hostedZone = HostedZone.fromLookup(
                this,
                "HostedZone",
                HostedZoneProviderProps.builder().domainName(hostedZoneDomain).build());

        Network.NetworkOutputParameters networkOutputParameters =
                Network.getOutputParametersFromParameterStore(this, appEnvironment);

        IApplicationLoadBalancer applicationLoadBalancer =
                ApplicationLoadBalancer.fromApplicationLoadBalancerAttributes(
                        this,
                        "LoadBalancer",
                        ApplicationLoadBalancerAttributes.builder()
                                .loadBalancerArn(networkOutputParameters.getLoadBalancerArn())
                                .securityGroupId(networkOutputParameters.getLoadBalancerSecurityGroupId())
                                .loadBalancerCanonicalHostedZoneId(
                                        networkOutputParameters.getLoadBalancerCanonicalHostedZoneId())
                                .loadBalancerDnsName(networkOutputParameters.getLoadBalancerDnsName())
                                .build());

        ARecord.Builder.create(this, "ARecord")
                .recordName(applicationDomain)
                .zone(hostedZone)
                .target(RecordTarget.fromAlias(new LoadBalancerTarget(applicationLoadBalancer)))
                .build();

        appEnvironment.tag(this);
    }
}
