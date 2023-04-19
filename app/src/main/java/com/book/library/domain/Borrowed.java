package com.book.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record Borrowed(
        @JsonProperty("FirstName") String firstName,
        @JsonProperty("LastName") String lastName,
        @JsonProperty("From")
                @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
                @JsonFormat(pattern = "d.M.yyyy")
                LocalDate from) {

    public Borrowed() {
        this(null, null, null);
    }
}
