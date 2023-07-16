package com.book.library.user;

public class ChangePassword {

    private final String userName;

    @ValidPassword
    private String password;

    @ValidPassword
    private String repeatPassword;

    public ChangePassword(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public boolean passwordsAreNotEqual() {
        return !password.equals(repeatPassword);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String password) {
        this.repeatPassword = password;
    }
}
