package com.nextit.library.dto;

public record BorrowedBookDto(long id, String name, String author, String borrowedTo,
                              String borrowedOn) implements BookDto {
}
