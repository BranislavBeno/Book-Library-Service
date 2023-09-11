package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reader")
public class ReaderRestController extends AbstractReaderController {

    private final ObservationRegistry registry;

    public ReaderRestController(@Autowired ReaderService service, @Autowired ObservationRegistry registry) {
        super(service);
        this.registry = registry;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<ReaderDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getService().findAllReaders(page).toList();
    }
}
