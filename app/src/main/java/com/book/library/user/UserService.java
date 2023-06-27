package com.book.library.user;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;

public interface UserService {

    void registerUser(Registration registration);

    AdminInitiateAuthResponse loginUser(User user);

    void refreshToken(User user, String refreshToken);

    void changePassword(ChangePassword changePassword);
}
