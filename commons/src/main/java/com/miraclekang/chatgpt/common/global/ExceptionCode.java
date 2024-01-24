package com.miraclekang.chatgpt.common.global;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    HttpStatus httpStatus();

    String code();

    record CommonExceptionCode(HttpStatus httpStatus, String code) implements ExceptionCode {
    }

    ExceptionCode OK = new CommonExceptionCode(HttpStatus.OK, "200");
    ExceptionCode BadRequest = new CommonExceptionCode(HttpStatus.BAD_REQUEST, "BadRequest");
    ExceptionCode IllegalState = new CommonExceptionCode(HttpStatus.BAD_REQUEST, "IllegalState");
    ExceptionCode Unauthorized = new CommonExceptionCode(HttpStatus.UNAUTHORIZED, "Unauthorized");
    ExceptionCode Forbidden = new CommonExceptionCode(HttpStatus.FORBIDDEN, "Forbidden");
    ExceptionCode NotFound = new CommonExceptionCode(HttpStatus.NOT_FOUND, "NotFound");
    ExceptionCode InternalServerError = new CommonExceptionCode(HttpStatus.INTERNAL_SERVER_ERROR, "InternalServerError");
    ExceptionCode NotImplemented = new CommonExceptionCode(HttpStatus.NOT_IMPLEMENTED, "NotImplemented");
}
