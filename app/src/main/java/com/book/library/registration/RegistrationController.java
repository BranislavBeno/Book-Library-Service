package com.book.library.registration;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final String REGISTRATION_ATTR = "registration";
    private static final String REGISTER_PAGE = "register";

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String getRegisterView(Model model) {
        model.addAttribute(REGISTRATION_ATTR, new Registration());
        return REGISTER_PAGE;
    }

    @PostMapping
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
            registrationService.registerUser(registration);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "You successfully registered for the Todo App. "
                            + "Please check your email inbox for further instructions.");
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/";

        } catch (CognitoIdentityProviderException exception) {

            model.addAttribute(REGISTRATION_ATTR, registration);
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("messageType", "danger");

            return REGISTER_PAGE;
        }
    }
}
