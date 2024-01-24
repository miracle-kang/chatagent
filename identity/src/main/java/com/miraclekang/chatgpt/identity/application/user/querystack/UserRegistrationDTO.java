package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.Registration;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegistrationDTO {

    @Schema(description = "Registration type")
    private Registration registrationType;
    @Schema(description = "Registration ID")
    private String registrationId;

    public UserRegistrationDTO(Registration registrationType, String registrationId) {
        this.registrationType = registrationType;
        this.registrationId = registrationId;
    }

    public static UserRegistrationDTO from(UserRegistration registration) {
        return new UserRegistrationDTO(
                registration.getRegistrationId().getType(),
                registration.getRegistrationId().getId()
        );
    }
}
