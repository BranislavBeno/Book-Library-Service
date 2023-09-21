package com.book.library.util;

import com.book.library.dto.BorrowedDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;

public final class BookUtils {

    private BookUtils() {}

    public static String createNonValidBorrowRequest() {
        return createNonValidRequest(new BorrowedDto(4, 1, getTomorrowDate()));
    }

    public static LocalDate getTomorrowDate() {
        return LocalDate.now().plusDays(1);
    }

    private static String createNonValidRequest(BorrowedDto dto) {
        try {
            JsonMapper jsonMapper =
                    JsonMapper.builder().addModule(new JavaTimeModule()).build();
            return jsonMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
