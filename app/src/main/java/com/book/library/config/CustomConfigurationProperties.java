package com.book.library.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "custom")
@Validated
public class CustomConfigurationProperties {

    @NotEmpty
    private Set<String> invitationCodes;

    @NotNull
    private Boolean useCognitoAsIdentityProvider = Boolean.FALSE;

    public Set<String> getInvitationCodes() {
        return invitationCodes;
    }

    public void setInvitationCodes(Set<String> invitationCodes) {
        this.invitationCodes = invitationCodes;
    }

    public boolean isUseCognitoAsIdentityProvider() {
        return useCognitoAsIdentityProvider;
    }

    public void setUseCognitoAsIdentityProvider(boolean useCognitoAsIdentityProvider) {
        this.useCognitoAsIdentityProvider = useCognitoAsIdentityProvider;
    }
}
