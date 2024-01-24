package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.Invitation;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationStatus;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvitationDTO {

    @Schema(description = "Invitation ID")
    private String invitationId;
    @Schema(description = "Invite code")
    private String inviteCode;
    @Schema(description = "Invitation Type")
    private InvitationType invitationType;
    @Schema(description = "Invitation Status")
    private InvitationStatus invitationStatus;


    public InvitationDTO(String invitationId, String inviteCode,
                         InvitationType invitationType,
                         InvitationStatus invitationStatus) {
        this.invitationId = invitationId;
        this.inviteCode = inviteCode;
        this.invitationType = invitationType;
        this.invitationStatus = invitationStatus;
    }

    public static InvitationDTO from(Invitation invitation) {
        return new InvitationDTO(
                invitation.getInvitationId().getId(),
                invitation.getInviteCode(),
                invitation.getInvitationType(),
                invitation.getInvitationStatus()
        );
    }

}
