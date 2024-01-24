package com.miraclekang.chatgpt.common.exception;

import com.miraclekang.chatgpt.common.global.ExceptionCode;

/**
 * 可以不捕获处理的领域异常
 */
public class DomainException extends RuntimeException {

    private final ExceptionCode exceptionCode;
    private final Object[] args;

    public DomainException(ExceptionCode exceptionCode, Object... args) {
        super(exceptionCode.code());
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, ExceptionCode exceptionCode, Object... args) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, Throwable cause, ExceptionCode exceptionCode, Object... args) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(Throwable cause, ExceptionCode exceptionCode, Object... args) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                           ExceptionCode exceptionCode, Object... args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Object[] getArgs() {
        return args;
    }
}
