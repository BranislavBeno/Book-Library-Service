package com.book.library.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class BookControllerAdvice {

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProblemDetail onBookNotFoundException() {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Book id is invalid");
    }

    @ExceptionHandler(BookDeletionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ProblemDetail onBookDeletionException() {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Book is still borrowed");
    }
}
