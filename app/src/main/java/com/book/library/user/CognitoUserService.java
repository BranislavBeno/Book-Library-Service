package com.book.library.user;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
public class CognitoUserService implements UserService {

    private final CognitoIdentityProviderClient cognitoIdentityProvider;
    private final String userPoolId;
    private final String clientId;

    public CognitoUserService(
            CognitoIdentityProviderClient cognitoIdentityProvider,
            @Value("${spring.security.oauth2.client.registration.cognito.poolId}") String userPoolId,
            @Value("${spring.security.oauth2.client.registration.cognito.clientId}") String clientId) {
        this.cognitoIdentityProvider = cognitoIdentityProvider;
        this.userPoolId = userPoolId;
        this.clientId = clientId;
    }

    @Override
    public void registerUser(Registration registration) {
        AdminCreateUserRequest registrationRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
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

        cognitoIdentityProvider.adminCreateUser(registrationRequest);
    }

    @Override
    public AdminInitiateAuthResponse loginUser(User user) {
        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of("USERNAME", user.getUsername(), "PASSWORD", user.getPassword()))
                .build();

        return cognitoIdentityProvider.adminInitiateAuth(authRequest);
    }
}
