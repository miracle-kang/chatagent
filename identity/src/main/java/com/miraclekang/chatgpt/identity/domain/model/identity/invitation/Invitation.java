package com.miraclekang.chatgpt.identity.domain.model.identity.invitation;

import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.User;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

/**
 * 邀请
 */
@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "UK_invitation_id", columnList = "invitationId", unique = true),
        @Index(name = "IDX_inviter_id", columnList = "inviterId"),
        @Index(name = "IDX_invite_code", columnList = "inviteCode", unique = true)
})
public class Invitation extends BaseEntity {

    // @Comment("Invitation ID")
    @AttributeOverride(name = "id", column = @Column(name = "invitationId", length = 64, nullable = false))
    private InvitationId invitationId;

    // @Comment("Inviter user ID")
    @AttributeOverride(name = "id", column = @Column(name = "inviterId", length = 64, nullable = false))
    private UserId inviterId;

    // @Comment("Invite code")
    @Column(length = 32)
    private String inviteCode;

    // @Comment("Invitation Type")
    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    private InvitationType invitationType;

    // @Comment("Invitation Status")
    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    private InvitationStatus invitationStatus;

    public Invitation(InvitationId invitationId, UserId inviterId, String inviteCode, InvitationType invitationType) {
        this.invitationId = invitationId;
        this.inviterId = inviterId;
        this.inviteCode = inviteCode;

        this.invitationType = invitationType == null ? InvitationType.ONE_TIME : invitationType;
        this.invitationStatus = InvitationStatus.AVAILABLE;
    }

    /**
     * Accept by user
     *
     * @param acceptedUser Accepted user
     */
    public void acceptBy(User acceptedUser) {
        Validate.isTrue(available(), "Invitation unavailable");

        acceptedUser.inviteFrom(this.getInvitationId(), this.getInviterId());

        if (this.invitationType == InvitationType.ONE_TIME) {
            this.invitationStatus = InvitationStatus.ACCEPTED;
        }
    }

    /**
     * Invalid this invitation
     */
    public void invalid(Requester requester) {
        Validate.isTrue(requester.isAdmin() || requester.isRequestUser(inviterId.getId()),
                "Only inviter can operate");

        this.invitationStatus = InvitationStatus.INVALID;
    }

    public boolean available() {
        return this.invitationStatus == InvitationStatus.AVAILABLE;
    }

    public static Invitation create(User inviter, InvitationType invitationType, InviteCodeGenerator generator) {
        return new Invitation(
                new InvitationId(IdentityGenerator.nextIdentity()),
                inviter.getUserId(),
                generator.generate(),
                invitationType);
    }
}
