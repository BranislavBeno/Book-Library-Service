package com.nextit.library.dto;

public sealed interface BookDto permits AnyBookDto, AvailableBookDto, BorrowedBookDto {
}
