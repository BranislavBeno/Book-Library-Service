package com.book.library.recommendation;

import io.micrometer.core.annotation.Timed;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookRecommendationController {

    public static final String MESSAGE_ATTR = "message";
    public static final String MESSAGE_TYPE_ATTR = "messageType";
    private final BookRecommendationService service;

    public BookRecommendationController(BookRecommendationService service) {
        this.service = service;
    }

    @Timed(value = "b-l-s.book.recommendation", description = "Measure the time how long it takes to recommend a book")
    @PostMapping("/{bookId}/recommend/{readerId}")
    public String recommendBook(
            @PathVariable int bookId, @PathVariable int readerId, RedirectAttributes redirectAttributes) {
        try {
            String recommencedTo = service.recommendBookTo(bookId, readerId);

            redirectAttributes.addFlashAttribute(
                    MESSAGE_ATTR, "You successfully recommended book to other reader %s.".formatted(recommencedTo));
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    MESSAGE_ATTR,
                    "Request processing failed: Invalid book id: %d or reader id: %d.".formatted(bookId, readerId));
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "failure");
        }

        return "redirect:/book/borrowed";
    }

    @GetMapping("/{bookId}/recommend/{readerId}/confirm")
    public String confirmRecommendation(
            @PathVariable int bookId,
            @PathVariable int readerId,
            @RequestParam String token,
            @AuthenticationPrincipal OidcUser user,
            RedirectAttributes redirectAttributes) {
        boolean confirmed = service.confirmRecommendation(user.getEmail(), bookId, readerId, token);
        if (confirmed) {
            redirectAttributes.addFlashAttribute(MESSAGE_ATTR, "You've confirmed book recommendation.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "success");
        } else {
            redirectAttributes.addFlashAttribute(MESSAGE_ATTR, "Invalid recommendation request.");
            redirectAttributes.addFlashAttribute(MESSAGE_TYPE_ATTR, "danger");
        }

        return "redirect:/book/borrowed";
    }
}
