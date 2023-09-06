package com.book.library.dto;

public sealed interface DataTransferObject permits AnyBookDto, AvailableBookDto, BorrowedBookDto {}
