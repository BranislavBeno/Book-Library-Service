package com.book.library.cdk.stack;

import com.book.library.cdk.construct.ApplicationEnvironment;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.ICertificate;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProviderProps;
import software.constructs.Construct;

public class CertificateStack extends Stack {

    public CertificateStack(
            final Construct scope,
            final String id,
            final Environment awsEnvironment,
            final ApplicationEnvironment applicationEnvironment,
            final String applicationDomain,
            final String hostedZoneDomain) {
        super(
                scope,
                id,
                StackProps.builder()
                        .stackName(applicationEnvironment.prefix("Certificate"))
                        .env(awsEnvironment)
                        .build());

        HostedZone.fromLookup(
                this,
                "HostedZone",
                HostedZoneProviderProps.builder().domainName(hostedZoneDomain).build());

        ICertificate websiteCertificate = Certificate.Builder.create(this, "WebsiteCertificate")
                .domainName(applicationDomain)
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
