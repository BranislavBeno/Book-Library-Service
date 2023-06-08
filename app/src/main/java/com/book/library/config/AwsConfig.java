package com.book.library.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public AWSCognitoIdentityProvider cognitoIdentityProvider(
            AwsRegionProvider regionProvider, AWSCredentialsProvider awsCredentialsProvider) {
        return AWSCognitoIdentityProviderClient.builder()
                .withCredentials(awsCredentialsProvider)
                .withRegion(regionProvider.getRegion())
                .build();
    }
}
