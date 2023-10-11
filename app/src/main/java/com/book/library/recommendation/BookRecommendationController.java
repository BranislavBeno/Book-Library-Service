package com.book.library.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookRecommendationController {

    private final BookRecommendationService service;

    public BookRecommendationController(@Autowired BookRecommendationService service) {
        this.service = service;
    }

    @PostMapping("/{bookId}/recommend/{readerId}")
    public String shareTodoWithCollaborator(
            @PathVariable("bookId") int bookId,
            @PathVariable("readerId") int readerId,
            RedirectAttributes redirectAttributes) {
        String recommencedTo = service.recommendBookTo(bookId, readerId);

        redirectAttributes.addFlashAttribute(
                "message",
                String.format(
                        "You successfully recommended book to other reader %s. "
                                + "Once the reader accepts the recommendation, you'll see her/him as a recommenced on the book.",
                        recommencedTo));
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/book/borrowed";
    }
}
