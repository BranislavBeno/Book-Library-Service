package com.book.library.cdk.stack;

import com.book.library.cdk.construct.ApplicationEnvironment;
import com.book.library.cdk.util.CdkUtil;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.certificatemanager.ICertificate;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

public class CertificateStack extends Stack {

    public CertificateStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment appEnvironment,
            final String applicationDomain,
            final String hostedZoneDomain) {
        super(
                scope,
                id,
                StackProps.builder()
                        .stackName(CdkUtil.createStackName("certificate", appEnvironment))
                        .env(awsEnvironment)
                        .build());

        IHostedZone hostedZone = HostedZone.fromLookup(
                this,
                "HostedZone",
                HostedZoneProviderProps.builder().domainName(hostedZoneDomain).build());

        ICertificate websiteCertificate = Certificate.Builder.create(this, "WebsiteCertificate")
                .domainName(applicationDomain)
                .validation(CertificateValidation.fromDns(hostedZone))
                .build();

        new CfnOutput(
                this,
                "sslCertificateArn",
                CfnOutputProps.builder()
                        .exportName("sslCertificateArn")
                        .value(websiteCertificate.getCertificateArn())
                        .build());
    }
}
