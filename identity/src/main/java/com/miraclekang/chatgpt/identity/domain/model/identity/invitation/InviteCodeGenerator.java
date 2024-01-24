package com.miraclekang.chatgpt.identity.domain.model.identity.invitation;

import org.apache.commons.lang3.RandomStringUtils;

public class InviteCodeGenerator {

    private final InvitationRepository invitationRepository;

    public InviteCodeGenerator(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public String generate() {
        String code = RandomStringUtils.randomAlphanumeric(8);
        while (invitationRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.randomAlphanumeric(8);
        }
        return code;
    }
}
