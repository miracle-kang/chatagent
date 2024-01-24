package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InvitationAccepterDTO {

    @Schema(description = "Accepter user ID")
    private String accepterUserId;
    @Schema(description = "Accepter username")
    private String accepterUsername;
    @Schema(description = "Accepter time")
    private LocalDateTime acceptedTime;

    @Schema(description = "Accepter profile")
    private UserPublicProfileDTO accepterProfile;

    public InvitationAccepterDTO(String accepterUserId, String accepterUsername, LocalDateTime acceptedTime,
                                 UserPublicProfileDTO accepterProfile) {
        this.accepterUserId = accepterUserId;
        this.accepterUsername = accepterUsername;
        this.acceptedTime = acceptedTime;
        this.accepterProfile = accepterProfile;
    }

    public static InvitationAccepterDTO from(User user) {
        return new InvitationAccepterDTO(
                user.getUserId().getId(),
                user.getUsername().protectUsername(),
                user.registerTime(),
                UserPublicProfileDTO.from(user.getProfile())
        );
    }
}
