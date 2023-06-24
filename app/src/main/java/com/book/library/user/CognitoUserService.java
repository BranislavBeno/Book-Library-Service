package com.book.library.user;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    private final String clientSecret;

    public CognitoUserService(
            CognitoIdentityProviderClient cognitoIdentityProvider,
            @Value("${spring.security.oauth2.client.registration.cognito.poolId}") String userPoolId,
            @Value("${spring.security.oauth2.client.registration.cognito.clientId}") String clientId,
            @Value("${spring.security.oauth2.client.registration.cognito.clientSecret}") String clientSecret) {
        this.cognitoIdentityProvider = cognitoIdentityProvider;
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
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
        String secretHash = calculateSecretHash(user.getUsername());

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", user.getUsername(),
                        "PASSWORD", user.getPassword(),
                        "SECRET_HASH", secretHash))
                .build();

        return cognitoIdentityProvider.adminInitiateAuth(authRequest);
    }

    private String calculateSecretHash(String userName) {
        String hMacSha256Algorithm = "HmacSHA256";
        SecretKeySpec signingKey =
                new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), hMacSha256Algorithm);
        try {
            Mac mac = Mac.getInstance(hMacSha256Algorithm);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHMac = mac.doFinal(clientId.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(rawHMac);
        } catch (Exception e) {
            throw new UserServiceException("Error while secret calculating.");
        }
    }
}
