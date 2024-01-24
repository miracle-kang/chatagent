package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.exception.DomainException;
import com.miraclekang.chatgpt.identity.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.Invitation;
import com.miraclekang.chatgpt.identity.domain.model.identity.invitation.InvitationRepository;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegisterService {

    private final PasswordService passwordService;
    private final UserIdentityCheckService identityCheckService;

    private final InvitationRepository invitationRepository;

    @Value("${auth.invitation-register:true}")
    private Boolean invitationRegister = true;

    public RegisterService(PasswordService passwordService,
                           UserIdentityCheckService identityCheckService,
                           InvitationRepository invitationRepository) {
        this.passwordService = passwordService;
        this.identityCheckService = identityCheckService;
        this.invitationRepository = invitationRepository;
    }

    public Administrator registerAdmin(Requester requester, Username username, Email email, Phone phone,
                                       String password, Boolean disabled, UserProfile profile) {
        if (requester != null && !requester.isAdmin()) {
            throw new DomainException(EnumExceptionCode.Forbidden);
        }
        return new Administrator(
                new AdministratorId(IdentityGenerator.nextIdentity()),
                privRegisterUser(UserType.Administrator, username, email, phone, password, disabled, profile),
                requester
        );
    }

    public Customer adminRegisterCustomer(Requester requester, Username username, Email email, Phone phone,
                                          String password, Boolean disabled, UserProfile profile) {
        if (!requester.isAdmin()) {
            throw new DomainException(EnumExceptionCode.Forbidden);
        }
        return new Customer(
                new CustomerId(IdentityGenerator.nextIdentity()),
                privRegisterUser(UserType.Customer, username, email, phone, password, disabled, profile),
                requester
        );
    }

    public Customer invitationRegisterCustomer(Username username, Email email, Phone phone, String password,
                                               Invitation invitation, UserProfile profile) {
        if (invitationRegister && invitation == null) {
            throw new DomainException(EnumExceptionCode.NeedInvitation);
        }

        User user = privRegisterUser(UserType.Customer, username, email, phone, password, false, profile);
        if (invitation != null) {
            if (!invitation.available()) {
                throw new DomainException(EnumExceptionCode.InvitationUnavailable);
            }

            invitation.acceptBy(user);
            invitationRepository.save(invitation);
        }
        return new Customer(
                new CustomerId(IdentityGenerator.nextIdentity()),
                user
        );
    }

    public Customer registrationRegisterCustomer(UserRegistration registration) {
        if (invitationRegister) {
            throw new DomainException(EnumExceptionCode.NeedInvitation);
        }
        return new Customer(
                new CustomerId(IdentityGenerator.nextIdentity()),
                privRegisterUser(
                        UserType.Customer,
                        registration.getUsername(),
                        registration.getEmail(),
                        registration.getPhone(),
                        null,
                        false,
                        new UserProfile(

                        )
                )
        );
    }

    private User privRegisterUser(UserType userType, Username username, Email email, Phone phone, String password,
                                  Boolean disabled, UserProfile profile) {
        Validate.notNull(username, "Username cannot be null.");
        if (email == null && phone == null) {
            throw new IllegalArgumentException("Email and phone cannot be empty at the same time.");
        }

        identityCheckService.checkDuplicate(null, username, email, phone);
        return new User(
                new UserId(IdentityGenerator.nextIdentity()),
                username,
                email,
                phone,
                password,
                userType,
                disabled,
                profile,
                passwordService
        );
    }

}
