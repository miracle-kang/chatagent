package com.miraclekang.chatgpt.common.global;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorMessage {

    private static final Object NULL_OBJECT = new Object();

    private final HttpStatus statusCode;
    private final ExceptionCode exceptionCode;
    private final Object content;

    private Object[] args;

    public ErrorMessage(ExceptionCode exceptionCode, Object[] args) {
        this.statusCode = exceptionCode.httpStatus();
        this.exceptionCode = exceptionCode;
        this.content = NULL_OBJECT;
        this.args = args;
    }

    public ErrorMessage(ExceptionCode exceptionCode, Object content, Object[] args) {
        this.statusCode = exceptionCode.httpStatus();
        this.exceptionCode = exceptionCode;
        this.content = content;
        this.args = args;
    }

    public ErrorMessage(ExceptionCode exceptionCode, Object content) {
        this.statusCode = exceptionCode.httpStatus();
        this.exceptionCode = exceptionCode;
        this.content = content;
    }

    public ErrorMessage(HttpStatus statusCode, ExceptionCode exceptionCode, Object content) {
        this.statusCode = statusCode;
        this.exceptionCode = exceptionCode;
        this.content = content;
    }

    public HttpStatus getStatus() {
        return statusCode == null ? exceptionCode.httpStatus() : statusCode;
    }
}
