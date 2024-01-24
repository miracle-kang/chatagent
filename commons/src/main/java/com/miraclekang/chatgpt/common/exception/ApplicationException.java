package com.miraclekang.chatgpt.common.exception;

import com.miraclekang.chatgpt.common.global.ExceptionCode;
import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private final Object[] args;

    public ApplicationException(ExceptionCode exceptionCode, Object... args) {
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, ExceptionCode exceptionCode, Object... args) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, Throwable cause, ExceptionCode exceptionCode, Object... args) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(Throwable cause, ExceptionCode exceptionCode, Object... args) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                                ExceptionCode exceptionCode, Object... args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public HttpStatus httpStatus() {
        return exceptionCode.httpStatus();
    }

    public String getCode() {
        return exceptionCode.code();
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        return exceptionCode.code();
    }
}
