package com.book.library.user;

import static org.mockito.Mockito.*;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

class CognitoUserServiceTest implements WithAssertions {

    private CognitoIdentityProviderClient providerClient;
    private CognitoUserService cut;

    @BeforeEach
    void setUp() {
        providerClient = mock(CognitoIdentityProviderClient.class);
        cut = new CognitoUserService(providerClient, "poolId", "clientId", "s3cr3d");
    }

    @Test
    void registerUser() {
        when(providerClient.adminCreateUser(any(AdminCreateUserRequest.class)))
                .thenReturn(any(AdminCreateUserResponse.class));

        cut.registerUser(new Registration());

        verify(providerClient).adminCreateUser(any(AdminCreateUserRequest.class));
    }

    @Test
    void loginUser() {
        when(providerClient.adminInitiateAuth(any(AdminInitiateAuthRequest.class)))
                .thenReturn(any(AdminInitiateAuthResponse.class));

        User user = new User();
        user.setUsername("duke");
        user.setPassword("passwd");
        cut.loginUser(user);

        verify(providerClient).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
    }

    @Test
    void changePassword() {
        when(providerClient.adminSetUserPassword(any(AdminSetUserPasswordRequest.class)))
                .thenReturn(any(AdminSetUserPasswordResponse.class));

        cut.changePassword(new ChangePassword("duke"));

        verify(providerClient).adminSetUserPassword(any(AdminSetUserPasswordRequest.class));
    }
}
