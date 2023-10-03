package com.book.library.dto;

import java.util.List;

public record AnyBookDto(int id, String name, String author, boolean borrowed, List<ReaderDto> offered)
        implements DataTransferObject {}
