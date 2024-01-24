package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;

@Embeddable
public class Email extends ValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$");

    // @Comment("Email Address")
    @Column(name = "email_address", length = 100)
    private String address;

    protected Email() {
    }

    public Email(String address) {
        Validate.isTrue(isValidEmailAddress(address), "Email address is invalid.");

        this.address = address;
    }

    public String getAddress() {
        return address;
    }


    public static boolean isValidEmailAddress(String emailAddress) {
        if (StringUtils.isBlank(emailAddress)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(emailAddress).matches();
    }

    public static Email of(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        return new Email(address);
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Email email = (Email) o;

        return new EqualsBuilder().append(address, email.address).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(address)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Email{" +
                "address='" + address + '\'' +
                '}';
    }
}
