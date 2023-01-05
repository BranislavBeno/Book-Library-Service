package com.nextit.library.dto;

public record AnyBookDto(long id, String name, String author, boolean borrowed) implements BookDto {
}
