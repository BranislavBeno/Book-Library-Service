package com.book.library.user;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;

public interface UserService {

    void registerUser(Registration registration);

    AdminInitiateAuthResponse loginUser(User user);

    AdminSetUserPasswordResponse changePassword(ChangePassword changePassword);
}
