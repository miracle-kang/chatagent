package com.miraclekang.chatgpt.identity.domain.model.identity.user;


import com.miraclekang.chatgpt.common.access.UserPrincipal;
import com.miraclekang.chatgpt.common.exception.DomainException;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import com.miraclekang.chatgpt.identity.domain.model.identity.VerificationService;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.Invitation;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationId;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationType;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InviteCodeGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_base", indexes = {
        @Index(name = "UK_user_id", columnList = "userId", unique = true),
        @Index(name = "UK_username", columnList = "username", unique = true),
        @Index(name = "UK_email_address", columnList = "email_address", unique = true),
        @Index(name = "UK_phone_number", columnList = "phone_number", unique = true),
        @Index(name = "IDX_invitation_id", columnList = "invitationId")
})
public class User extends BaseEntity {

    @Embedded
    // @Comment("User ID")
    @AttributeOverride(name = "id", column = @Column(name = "userId", nullable = false))
    private UserId userId;

    // @Comment("Username(uid)")
    private Username username;

    // @Comment("User Email")
    @AttributeOverride(name = "address", column = @Column(unique = true))
    private Email email;

    // @Comment("User Phone")
    @AttributeOverride(name = "number", column = @Column(unique = true))
    private Phone phone;

    // @Comment("User password")
    @Column(length = 64)
    private String password;

    // @Comment("User type")
    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    // @Comment("User enabled")
    private Boolean disabled;

    // @Comment("User latest login time")
    private LocalDateTime latestLogin;

    // @Comment("Invitation ID")
    @AttributeOverride(name = "id", column = @Column(name = "invitationId", length = 64))
    private InvitationId invitationId;
    // @Comment("Inviter user")
    @AttributeOverride(name = "id", column = @Column(name = "inviterId", length = 64))
    private UserId inviter;

    @OneToOne(cascade = CascadeType.ALL)
    private UserProfile profile;

    public User(UserId userId, Username username, Email email, Phone phone, String planPassword,
                UserType userType, Boolean disabled, UserProfile profile,
                PasswordService passwordService) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.disabled = disabled;
        this.profile = profile;

        String randPassword = null;
        if (StringUtils.isNotBlank(planPassword)) {
            Validate.isTrue(planPassword.length() >= 6, "Password length must be >= 6");
            this.password = passwordService.encryptPassword(planPassword, userId.getId());
        } else {
            randPassword = passwordService.randPassword();
            this.password = passwordService.encryptPassword(randPassword, userId.getId());
        }

        this.registerEvent(new UserRegistered(
                userId,
                username,
                email,
                phone,
                randPassword,
                userType
        ));
    }

    public User update(Requester requester, Username username, Email email, Phone phone,
                       UserIdentityCheckService identityCheckService) {
        if (!requester.isAdmin() && !requester.isRequestUser(this.userId.getId())) {
            throw new DomainException("", EnumExceptionCode.Forbidden);
        }
        identityCheckService.checkDuplicate(this, username, email, phone);

        if (username != null) {
            this.username = username;
        }
        if (email != null) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
        return this;
    }

    public UserProfile updateProfile(Requester requester, UserProfile profile) {
        if (!requester.isAdmin() && !requester.isRequestUser(this.userId.getId())) {
            throw new DomainException("", EnumExceptionCode.Forbidden);
        }
        this.profile = this.profile == null ? profile : this.profile.update(profile);

        return profile;
    }

    public void changePassword(Requester requester, String planPassword,
                               String newPlanPassword, PasswordService passwordService) {
        if (!requester.isAdmin() && !requester.isRequestUser(this.userId.getId())) {
            throw new DomainException("", EnumExceptionCode.Forbidden);
        }
        String encryptedPassword = passwordService.encryptPassword(planPassword, this.getUserId().getId());
        if (!StringUtils.equals(this.getPassword(), encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }

        Validate.isTrue(newPlanPassword.length() >= 6, "Password length must be greater than 6");
        this.password = passwordService.encryptPassword(newPlanPassword, userId.getId());
    }

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }

    public void resetPassword(Requester requester, PasswordService passwordService) {
        if (!requester.isAdmin()) {
            throw new DomainException(EnumExceptionCode.Forbidden);
        }

        String randPassword = passwordService.randPassword();
        this.password = passwordService.encryptPassword(randPassword, userId.getId());

        this.registerEvent(new UserPasswordReset(
                userId,
                username,
                email,
                phone,
                randPassword
        ));
    }

    public UserPrincipal loginByEmailAuthCode(VerificationService verificationService, String authCode) {
        Boolean checked = verificationService.checkVerificationCode(email, authCode);
        if (BooleanUtils.isNotTrue(checked)) {
            throw new IllegalArgumentException("Invalid auth code");
        }
        return login();
    }

    public UserPrincipal loginByPhoneAuthCode(VerificationService verificationService, String authCode) {
        Boolean checked = verificationService.checkVerificationCode(phone, authCode);
        if (BooleanUtils.isNotTrue(checked)) {
            throw new IllegalArgumentException("Invalid auth code");
        }
        return login();
    }

    public UserPrincipal loginByPassword(PasswordService passwordService, String planPassword) {
        if (StringUtils.isBlank(planPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }

        String encryptedPassword = passwordService.encryptPassword(planPassword, this.getUserId().getId());
        if (!StringUtils.equals(this.getPassword(), encryptedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
        return login();
    }

    public UserPrincipal login() {
        if (BooleanUtils.isTrue(disabled)) {
            throw new DomainException(EnumExceptionCode.Forbidden);
        }
        latestLogin = LocalDateTime.now();
        return new UserPrincipal(userId.getId(), username.getUsername(), userType.name());
    }

    public Invitation newInvitation(InvitationType type, InviteCodeGenerator generator) {
        return Invitation.create(this, type, generator);
    }

    public void inviteFrom(InvitationId invitationId, UserId inviter) {
        this.invitationId = invitationId;
        this.inviter = inviter;
    }

    public UserInfo userInfo() {
        return new UserInfo(userId, username, email, phone, userType, profile);
    }

    public LocalDateTime registerTime() {
        return super.getCreatedAt();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + username + '\'' +
                ", email=" + email +
                ", phone=" + phone +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                ", disabled=" + disabled +
                ", latestLogin=" + latestLogin +
                '}';
    }
}
