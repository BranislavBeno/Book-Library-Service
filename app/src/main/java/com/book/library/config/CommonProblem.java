package com.book.library.config;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.zalando.problem.Status.FORBIDDEN;
import static org.zalando.problem.Status.UNAUTHORIZED;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;

@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties({"stackTrace", "type", "title", "message", "localizedMessage", "parameters"})
public class CommonProblem extends AbstractThrowableProblem {

    private CommonProblem(StatusType status, String detail) {
        super(null, null, status, detail, null, null, null);
    }

    public static CommonProblem unauthorized() {
        return new CommonProblem(UNAUTHORIZED, "Unauthorised or bad credentials.");
    }

    public static CommonProblem forbidden() {
        return new CommonProblem(FORBIDDEN, "Forbidden due to insufficient access rights.");
    }
}
