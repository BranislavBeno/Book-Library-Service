package com.book.library;

import com.book.library.tracing.TracingEvent;
import java.security.Principal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EventController {

    private final ApplicationEventPublisher publisher;

    public EventController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping
    @RequestMapping("/book/all")
    public String getAllBooks(Principal principal) {
        TracingEvent event = new TracingEvent(this, "index", principal != null ? principal.getName() : "anonymous");
        publisher.publishEvent(event);

        return "index";
    }
}
