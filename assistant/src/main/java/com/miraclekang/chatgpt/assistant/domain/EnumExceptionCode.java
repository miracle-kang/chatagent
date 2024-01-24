package com.miraclekang.chatgpt.assistant.domain;

import com.miraclekang.chatgpt.common.global.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public enum EnumExceptionCode implements ExceptionCode {
    // Global
    NeedInvitation(HttpStatus.BAD_REQUEST, "NeedInvitation"),
    InvitationUnavailable(HttpStatus.BAD_REQUEST, "InvitationUnavailable"),
    ;


    private final HttpStatus status;
    private final String code;

    EnumExceptionCode(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    public HttpStatus httpStatus() {
        return status;
    }

    public String code() {
        return StringUtils.isBlank(code) ? this.name() : this.code;
    }
}
