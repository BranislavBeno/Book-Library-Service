package com.book.library.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

class CognitoUserServiceTest implements WithAssertions {

    private CognitoIdentityProviderClient providerClient;
    private CognitoUserService cut;

    @BeforeEach
    void setUp() {
        providerClient = Mockito.mock(CognitoIdentityProviderClient.class);
        cut = new CognitoUserService(providerClient, "poolId", "clientId", "s3cr3d");
    }

    @Test
    void registerUser() {
        Mockito.when(providerClient.adminCreateUser(Mockito.any(AdminCreateUserRequest.class)))
                .thenReturn(Mockito.any(AdminCreateUserResponse.class));

        cut.registerUser(new Registration());

        Mockito.verify(providerClient).adminCreateUser(Mockito.any(AdminCreateUserRequest.class));
    }

    @Test
    void loginUser() {
        Mockito.when(providerClient.adminInitiateAuth(Mockito.any(AdminInitiateAuthRequest.class)))
                .thenReturn(Mockito.any(AdminInitiateAuthResponse.class));

        User user = new User();
        user.setUsername("duke");
        user.setPassword("passwd");
        cut.loginUser(user);

        Mockito.verify(providerClient).adminInitiateAuth(Mockito.any(AdminInitiateAuthRequest.class));
    }

    @Test
    void changePassword() {
        Mockito.when(providerClient.adminSetUserPassword(Mockito.any(AdminSetUserPasswordRequest.class)))
                .thenReturn(Mockito.any(AdminSetUserPasswordResponse.class));

        cut.changePassword(new ChangePassword("duke"));

        Mockito.verify(providerClient).adminSetUserPassword(Mockito.any(AdminSetUserPasswordRequest.class));
    }
}
