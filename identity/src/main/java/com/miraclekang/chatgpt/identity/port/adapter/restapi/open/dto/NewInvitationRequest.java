package com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto;

import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NewInvitationRequest {

    @Schema(description = "Invitation Type")
    private InvitationType type;
}
