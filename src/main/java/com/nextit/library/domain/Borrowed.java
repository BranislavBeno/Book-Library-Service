package com.nextit.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record Borrowed(
        @JsonProperty("FirstName") String firstName,
        @JsonProperty("LastName") String lastName,
        @JsonProperty("From") @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) @JsonFormat(pattern = "d.M.yyyy") LocalDate from) {
}
