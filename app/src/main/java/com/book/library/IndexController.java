package com.book.library;

import com.book.library.tracing.TracingEvent;
import java.security.Principal;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    private final ApplicationEventPublisher publisher;

    public IndexController(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping
    @RequestMapping("/")
    public String getIndex(Principal principal) {
        TracingEvent event = new TracingEvent(this, "index", principal != null ? principal.getName() : "anonymous");
        publisher.publishEvent(event);

        return "index";
    }
}
