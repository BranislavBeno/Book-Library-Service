package com.book.library.reader;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ReaderControllerAdvice {

    @ExceptionHandler(ReaderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ProblemDetail onReaderNotFoundException() {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), "Reader id is invalid");
    }

    @ExceptionHandler(ReaderDeletionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ProblemDetail onReaderDeletionException() {
        return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), "Reader still has borrowed books");
    }
}
