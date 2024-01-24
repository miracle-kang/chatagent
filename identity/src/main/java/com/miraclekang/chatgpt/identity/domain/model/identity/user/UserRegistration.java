package com.miraclekang.chatgpt.identity.domain.model.identity.user;

import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.common.repo.Converters;
import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(indexes = {
        @Index(name = "UK_registration_id", columnList = "registrationId, registrationType", unique = true),
        @Index(name = "IDX_registration_user_id", columnList = "userId")
})
public class UserRegistration extends BaseEntity {

    // @Comment("User ID")
    @AttributeOverride(name = "id", column = @Column(name = "registrationId", nullable = false))
    private UserRegistrationId registrationId;

    @AttributeOverride(name = "id", column = @Column(name = "userId"))
    private UserId userId;

    // @Comment("Registration info")
    @Column(columnDefinition = "TEXT")
    @Convert(converter = Converters.StringMapConverter.class)
    private Map<String, String> registrationInfo;

    protected UserRegistration() {
    }

    public UserRegistration(UserRegistrationId registrationId, Map<String, String> registrationInfo) {
        this.registrationId = registrationId;
        this.registrationInfo = registrationInfo;
    }

    public void update(Map<String, Object> attributes) {
        this.registrationInfo = attributes.entrySet().stream()
                .map(entry -> entry.getValue() == null ? null : Map.entry(
                        entry.getKey(),
                        entry.getValue().toString()))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Registration registration() {
        return registrationId.getType();
    }

    public void boundTo(User user) {
        Objects.requireNonNull(user, "User is null");

        this.userId = user.getUserId();
    }

    public boolean bounded() {
        return userId != null;
    }

    public Username getUsername() {
        return registration().getUID(registrationInfo);
    }

    public String getName() {
        return registration().getNickName(registrationInfo);
    }

    public Phone getPhone() {
        return registration().getPhone(registrationInfo);
    }

    public Email getEmail() {
        return registration().getEmail(registrationInfo);
    }

    public static UserRegistration of(UserRegistrationId registrationId, Map<String, Object> attributes) {
        return new UserRegistration(
                registrationId,
                attributes.entrySet().stream()
                        .map(entry -> entry.getValue() == null ? null : Map.entry(
                                entry.getKey(),
                                entry.getValue().toString()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }
}
