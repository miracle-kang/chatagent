package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.model.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;

@Getter
@Embeddable
public class Username extends ValueObject {

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z][a-zA-Z0-9_-]+$");


    // @Comment("Username(uid)")
    @Column(name = "username", length = 32)
    private String username;

    protected Username() {
    }

    public Username(String username) {
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Username is invalid.");
        }
        this.username = username;
    }

    public String protectUsername() {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return username.charAt(0) + "****" + username.substring(username.length() - 1);
    }

    public static boolean isValidUsername(String username) {
        return StringUtils.isNotBlank(username)
                && username.length() >= 4 && username.length() <= 32
                && USERNAME_PATTERN.matcher(username).matches();
    }

    public static Username of(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        return new Username(username);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Username username1 = (Username) o;

        return new EqualsBuilder().append(username, username1.username).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(username).toHashCode();
    }
}
