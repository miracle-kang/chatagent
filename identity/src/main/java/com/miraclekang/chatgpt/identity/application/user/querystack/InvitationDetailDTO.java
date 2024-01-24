package com.miraclekang.chatgpt.identity.application.user.querystack;


import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.Invitation;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationStatus;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationType;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InvitationDetailDTO extends InvitationDTO {

    @Schema(description = "Top 10 accepters")
    private List<InvitationAccepterDTO> topAccepters;

    public InvitationDetailDTO(String invitationId, String inviteCode,
                               InvitationType invitationType,
                               InvitationStatus invitationStatus,
                               List<InvitationAccepterDTO> topAccepters) {
        super(invitationId, inviteCode, invitationType, invitationStatus);
        this.topAccepters = topAccepters;
    }


    public static InvitationDTO from(Invitation invitation, UserRepository userRepository) {
        return new InvitationDetailDTO(
                invitation.getInvitationId().getId(),
                invitation.getInviteCode(),
                invitation.getInvitationType(),
                invitation.getInvitationStatus(),
                userRepository.top10InvitedUsers(invitation.getInviterId()).stream()
                        .map(InvitationAccepterDTO::from)
                        .toList()
        );
    }
}
