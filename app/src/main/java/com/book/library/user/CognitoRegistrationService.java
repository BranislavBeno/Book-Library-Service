package com.book.library.user;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

public class CognitoRegistrationService implements RegistrationService {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;
    private final MeterRegistry meterRegistry;
    private final String userPooldId;

    public CognitoRegistrationService(
            CognitoIdentityProviderClient cognitoIdentityProviderClient,
            MeterRegistry meterRegistry,
            String userPoolId) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
        this.meterRegistry = meterRegistry;
        this.userPooldId = userPoolId;
    }

    @Override
    public void registerUser(Registration registration) {
        AdminCreateUserRequest registrationRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPooldId)
                .username(registration.getUsername())
                .userAttributes(
                        AttributeType.builder()
                                .name("email")
                                .value(registration.getEmail())
                                .build(),
                        AttributeType.builder()
                                .name("name")
                                .value(registration.getUsername())
                                .build(),
                        AttributeType.builder()
                                .name("email_verified")
                                .value("true")
                                .build())
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .forceAliasCreation(Boolean.FALSE)
                .build();

        cognitoIdentityProviderClient.adminCreateUser(registrationRequest);

        Counter successCounter = Counter.builder("b-l-s.registration.signups")
                .description("Number of user registrations")
                .tag("outcome", "success")
                .register(meterRegistry);

        successCounter.increment();
    }
}
