package com.miraclekang.chatgpt.common.access;

import com.miraclekang.chatgpt.common.model.ValueObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.security.Principal;

public class UserPrincipal extends ValueObject implements Principal {

    private final String userId;
    private final String username;
    private final String userType;

    public UserPrincipal(String userId, String username, String userType) {
        this.userId = userId;
        this.username = username;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return username;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserPrincipal principal = (UserPrincipal) o;

        return new EqualsBuilder()
                .append(userId, principal.userId)
                .append(username, principal.username)
                .append(userType, principal.userType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId).append(username).append(userType)
                .toHashCode();
    }
}
