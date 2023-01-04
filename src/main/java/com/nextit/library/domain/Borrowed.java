package com.nextit.library.domain;

import java.time.LocalDate;

public record Borrowed(String firstName, String lastName, LocalDate from) {
}
