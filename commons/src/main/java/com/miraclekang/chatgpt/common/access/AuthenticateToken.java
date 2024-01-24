package com.miraclekang.chatgpt.common.access;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
public class AuthenticateToken extends ValueObject {

    private final String token;
    private final ZonedDateTime expiresTime;

    public AuthenticateToken(String token, ZonedDateTime expiresTime) {
        this.token = token;
        this.expiresTime = expiresTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticateToken that = (AuthenticateToken) o;
        return Objects.equals(token, that.token) && Objects.equals(expiresTime, that.expiresTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expiresTime);
    }

    @Override
    public String toString() {
        return "AuthenticateToken{" +
                "token='" + token + '\'' +
                ", expiresTime=" + expiresTime +
                '}';
    }
}
