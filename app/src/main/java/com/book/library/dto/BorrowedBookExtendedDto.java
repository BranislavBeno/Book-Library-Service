package com.book.library.dto;

import java.time.LocalDate;
import java.util.List;

public record BorrowedBookExtendedDto(
        int id,
        LocalDate borrowedOn,
        String name,
        String author,
        int readerId,
        String firstName,
        String lastName,
        List<ReaderDto> candidates)
        implements DataTransferObject {

    public BorrowedBookExtendedDto(BorrowedBookDto dto, List<ReaderDto> candidates) {
        this(
                dto.id(),
                dto.borrowedOn(),
                dto.name(),
                dto.author(),
                dto.readerId(),
                dto.firstName(),
                dto.lastName(),
                candidates);
    }

    public String readerName() {
        return "%s %s".formatted(firstName(), lastName());
    }

    @Override
    public String toString() {
        return "Book[id=%d, name='%s', author='%s'', borrowedTo='%s', borrowedOn='%s']"
                .formatted(id, name, author, readerName(), borrowedOn);
    }
}
