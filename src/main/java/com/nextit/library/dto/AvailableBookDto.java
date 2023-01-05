package com.nextit.library.dto;

public record AvailableBookDto(long id, String name, String author) implements BookDto {
}
