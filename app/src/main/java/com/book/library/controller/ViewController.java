package com.book.library.controller;

import com.book.library.dto.DataTransferObject;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;

public interface ViewController {

    default List<Integer> providePageNumbers(int totalPages) {
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages).boxed().toList();
        }

        return Collections.emptyList();
    }

    default <T extends DataTransferObject> PageData<T> providePageData(Page<T> page) {
        List<Integer> pageNumbers = providePageNumbers(page.getTotalPages());

        return new PageData<>(page, pageNumbers);
    }

    record PageData<T extends DataTransferObject>(Page<T> dtoPage, List<Integer> pageNumbers) {}
}
