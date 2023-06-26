package com.book.library.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

@Controller
public class UserController {

    private static final String MESSAGE_ATTR = "message";
    private static final String MESSAGE_TYPE_ATTR = "messageType";
    private static final String DANGER_ATTR = "danger";
    private static final String REGISTRATION_ATTR = "registration";
    private static final String USER_ATTR = "user";
    private static final String CHANGE_PASSWD_ATTR = "changePassword";
    private static final String REGISTER_PAGE = "register";
    private static final String LOGIN_PAGE = "login";
    private static final String CHANGE_PASSWD_PAGE = "change-password";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegisterView(Model model) {
        model.addAttribute(REGISTRATION_ATTR, new Registration());

        return REGISTER_PAGE;
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid Registration registration,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(REGISTRATION_ATTR, registration);

            return REGISTER_PAGE;
        }

        try {
            userService.registerUser(registration);

            redirectAttributes.addFlashAttribute(
                    MESSAGE_ATTR,
                    "You successfully registered for the Book Library App. "
                            + "Please check your email inbox for further instructions.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "success");

            return "redirect:/login";

        } catch (CognitoIdentityProviderException exception) {

            model.addAttribute(REGISTRATION_ATTR, registration);
            model.addAttribute(MESSAGE_ATTR, exception.getMessage());
            model.addAttribute(MESSAGE_TYPE_ATTR, DANGER_ATTR);

            return REGISTER_PAGE;
        }
    }

    @GetMapping("/login")
    public String getLoginView(Model model) {
        model.addAttribute(USER_ATTR, new User());

        return LOGIN_PAGE;
    }

    @PostMapping("/login")
    public String loginUser(
            @Valid User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(USER_ATTR, user);

            return LOGIN_PAGE;
        }

        try {
            AdminInitiateAuthResponse response = userService.loginUser(user);
            ChallengeNameType challengeNameType = response.challengeName();

            if (challengeNameType.equals(ChallengeNameType.NEW_PASSWORD_REQUIRED)) {
                redirectAttributes.addFlashAttribute(
                        MESSAGE_ATTR,
                        """
                                Please create a new password and make sure that will contain:
                                - at least 12 characters
                                - at least 1 number
                                - at least 1 special character
                                - at least 1 uppercase letter
                                - at least 1 lowercase letter""");
                redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "warning");

                return "redirect:/change-password";
            }

            return "redirect:/";

        } catch (CognitoIdentityProviderException exception) {

            model.addAttribute(USER_ATTR, user);
            model.addAttribute(MESSAGE_ATTR, exception.getMessage());
            model.addAttribute(MESSAGE_TYPE_ATTR, DANGER_ATTR);

            return LOGIN_PAGE;
        }
    }

    @GetMapping("/change-password")
    public String getChangePasswordView(Model model) {
        model.addAttribute(CHANGE_PASSWD_ATTR, new ChangePassword());

        return CHANGE_PASSWD_PAGE;
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid ChangePassword changePassword,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(CHANGE_PASSWD_ATTR, changePassword);

            return CHANGE_PASSWD_PAGE;
        }

        if (changePassword.proposedAreNotEqual()) {
            model.addAttribute(CHANGE_PASSWD_ATTR, changePassword);
            model.addAttribute(MESSAGE_ATTR, "Proposed passwords are not equal.");
            model.addAttribute(MESSAGE_TYPE_ATTR, DANGER_ATTR);

            return CHANGE_PASSWD_PAGE;
        }

        try {
            userService.changePassword(changePassword, "");

            redirectAttributes.addFlashAttribute(MESSAGE_ATTR, "You successfully changed password.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "success");

            return "redirect:/login";

        } catch (CognitoIdentityProviderException exception) {

            model.addAttribute(CHANGE_PASSWD_ATTR, changePassword);
            model.addAttribute(MESSAGE_ATTR, exception.getMessage());
            model.addAttribute(MESSAGE_TYPE_ATTR, DANGER_ATTR);

            return CHANGE_PASSWD_PAGE;
        }
    }
}
