package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ReaderDto add(@Valid @RequestBody ReaderDto dto) {
        return Observation.createNotStarted("addition.reader", this.registry).observe(() -> updateReader(dto));
    }
}
