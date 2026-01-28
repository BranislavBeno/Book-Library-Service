package com.book.library.util;

import com.book.library.dto.BorrowedDto;
import java.time.LocalDate;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

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
            JsonMapper jsonMapper = JsonMapper.builder()
                    .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
                    .build();
            return jsonMapper.writeValueAsString(dto);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
