package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/v1/reader")
public class ReaderRestController extends AbstractReaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderRestController.class);
    private static final String MESSAGE = "Reader %s failed. Reader with id='%d' not found.";

    private final ObservationRegistry registry;

    public ReaderRestController(@Autowired ReaderService service, @Autowired ObservationRegistry registry) {
        super(service);
        this.registry = registry;
    }

    @GetMapping("/all")
    public List<ReaderDto> all(@RequestParam(name = "page", defaultValue = "0") int page) {
        return getService().findAllReaders(page).toList();
    }

    @PostMapping("/add")
    public ReaderDto add(@Valid @RequestBody ReaderDto dto) {
        return Observation.createNotStarted("addition.reader", this.registry).observe(() -> updateReader(dto));
    }

    @PutMapping("/update")
    public ReaderDto update(@Valid @RequestBody ReaderDto dto) {
        int id = dto.getId();
        if (!getService().readerExists(id)) {
            handleReaderNotFound("updating", id);
        }

        return Observation.createNotStarted("updating.reader.id", this.registry).observe(() -> updateReader(dto));
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam("readerId") int id) {
        if (!getService().readerExists(id)) {
            handleReaderNotFound("deletion", id);
        }

        Observation.createNotStarted("deletion.reader.id", this.registry).observe(() -> deleteReader(id));
    }

    private static void handleReaderNotFound(String operation, int id) {
        String message = MESSAGE.formatted(operation, id);
        LOGGER.error(message);
        throw new ReaderNotFoundException(message);
    }
}
