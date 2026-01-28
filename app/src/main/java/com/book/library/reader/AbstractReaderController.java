package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

abstract class AbstractReaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReaderController.class);

    private final ReaderService service;

    AbstractReaderController(ReaderService service) {
        this.service = service;
    }

    ReaderDto findReader(int id) {
        Reader reader = service.findReader(id);

        return new ReaderDto(Objects.requireNonNullElseGet(reader, Reader::new));
    }

    ReaderDto updateReader(ReaderDto dto) {
        Reader reader = service.findReader(dto.getId());

        if (reader == null) {
            reader = new Reader();
        }

        reader.setId(dto.getId());
        reader.setFirstName(dto.getFirstName());
        reader.setLastName(dto.getLastName());
        reader.setEmail(dto.getEmail());

        ReaderDto readerDto = new ReaderDto(service.saveReader(reader));

        String message = "%s saved into repository.".formatted(readerDto.toString());
        LOGGER.info(message);

        return readerDto;
    }

    void deleteReader(int id) {
        try {
            service.deleteReader(id);
            String message = "Reader with id='%d' deleted successfully.".formatted(id);
            LOGGER.info(message);
        } catch (DataIntegrityViolationException _) {
            String message =
                    "Reader with id='%d' can't be deleted due to he/she still has borrowed books.".formatted(id);
            LOGGER.error(message);
            throw new ReaderDeletionException(message);
        }
    }

    ReaderService getService() {
        return service;
    }
}
