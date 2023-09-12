package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractReaderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReaderController.class);

    private final ReaderService service;

    AbstractReaderController(ReaderService service) {
        this.service = service;
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

    ReaderService getService() {
        return service;
    }
}
