package com.book.library.registration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
public class LocalRegistrationService implements RegistrationService {

    @Override
    public void registerUser(Registration registration) {
        // This implementation serves only for testing purpose.
        // No real registration is required, due to usage of a local Keycloak instance with a pre-defined set of users
    }
}
