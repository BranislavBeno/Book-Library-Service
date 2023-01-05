package com.nextit.library.dto;

public record BookBorrowedDto(long id, String name, String author, String borrowedTo, String borrowedOn) {
}
