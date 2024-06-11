package com.book.library.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"stackTrace", "type", "title", "message", "localizedMessage", "parameters"})
public class CommonProblem extends AbstractThrowableProblem {

    private CommonProblem(StatusType status, String detail) {
        super(null, null, status, detail, null, null, null);
    }

    public static CommonProblem unauthorized() {
        return new CommonProblem(Status.UNAUTHORIZED, "Unauthorised or bad credentials.");
    }

    public static CommonProblem forbidden() {
        return new CommonProblem(Status.FORBIDDEN, "Forbidden due to insufficient access rights.");
    }
}
