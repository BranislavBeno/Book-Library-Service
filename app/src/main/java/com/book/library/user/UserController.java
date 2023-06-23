package com.book.library.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

@Controller
public class UserController {

    private static final String REGISTRATION_ATTR = "registration";
    private static final String REGISTER_PAGE = "register";
    private static final String LOGIN_PAGE = "login";

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
                    "message",
                    "You successfully registered for the Todo App. "
                            + "Please check your email inbox for further instructions.");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/login";

        } catch (CognitoIdentityProviderException exception) {

            model.addAttribute(REGISTRATION_ATTR, registration);
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("messageType", "danger");

            return REGISTER_PAGE;
        }
    }

    @GetMapping("/login")
    public String getLoginView() {
        return LOGIN_PAGE;
    }
}
