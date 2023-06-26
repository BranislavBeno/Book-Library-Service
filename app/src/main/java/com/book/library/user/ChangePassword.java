package com.book.library.user;

import jakarta.validation.constraints.NotBlank;

public class ChangePassword {

    @NotBlank
    private String previousPassword;

    @NotBlank
    private String proposedPassword1;

    @NotBlank
    private String proposedPassword2;

    public boolean proposedAreNotEqual() {
        return !proposedPassword1.equals(proposedPassword2);
    }

    public String getPreviousPassword() {
        return previousPassword;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public String getProposedPassword1() {
        return proposedPassword1;
    }

    public void setProposedPassword1(String proposedPassword) {
        this.proposedPassword1 = proposedPassword;
    }

    public String getProposedPassword2() {
        return proposedPassword2;
    }

    public void setProposedPassword2(String proposedPassword2) {
        this.proposedPassword2 = proposedPassword2;
    }
}
