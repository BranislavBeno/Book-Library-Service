package com.book.library.user;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
public class LocalUserService implements UserService {

    @Override
    public void registerUser(Registration registration) {
        // This implementation serves only for testing purpose.
        // No real registration is required, due to usage of a local Keycloak instance with a pre-defined set of users
    }

    @Override
    public AdminInitiateAuthResponse loginUser(User user) {
        // This implementation serves only for testing purpose.
        return null;
    }

    @Override
    public void refreshToken(User user, String refreshToken) {
        // This implementation serves only for testing purpose.
    }

    @Override
    public void changePassword(ChangePassword changePassword) {
        // This implementation serves only for testing purpose.
    }
}
