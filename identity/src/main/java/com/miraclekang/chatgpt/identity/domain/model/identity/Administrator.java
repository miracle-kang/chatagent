package com.miraclekang.chatgpt.identity.domain.model.identity;


import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.identity.domain.model.access.Role;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "UK_administrator_id", columnList = "administratorId", unique = true)
})
public class Administrator extends AggregateRoot {

    @Embedded
    // @Comment("Admin ID")
    @AttributeOverride(name = "id", column = @Column(name = "administratorId", length = 64, nullable = false))
    private AdministratorId administratorId;

    // @Comment("Owner User")
    @JoinColumn(name = "userId")
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @ElementCollection
    @CollectionTable(name = "administrator_role", joinColumns = {
            @JoinColumn(name = "administratorId"),
    })
    private final List<AdministratorRole> roles = new LinkedList<>();

    @Embedded
    // @Comment("Operator")
    @AttributeOverrides( value = {
            @AttributeOverride(name = "userId", column = @Column(name = "operatorUserId", length = 64)),
            @AttributeOverride(name = "userName", column = @Column(name = "operatorUserName", length = 128)),
            @AttributeOverride(name = "userIp", column = @Column(name = "operatorUserIp", length = 64))
    })
    private OperatorUser operator;

    public Administrator(AdministratorId administratorId, User user, Requester requester) {
        this.administratorId = administratorId;
        this.user = user;

        this.operator = OperatorUser.requester(requester);

        this.andEventsFrom(user);
    }

    public void assignRole(Role role, Requester requester) {
        this.assignRoles(List.of(role), requester);
    }

    public void assignRoles(List<Role> roles, Requester requester) {
        this.roles.addAll(roles.stream()
                .map(role -> new AdministratorRole(role.getRoleId(), role.getName()))
                .toList());
        this.operator = OperatorUser.requester(requester);
    }

    public void update(Username username, Email email, Phone phone, UserProfile profile,
                       Requester requester, UserIdentityCheckService identityCheckService) {

        this.user.update(requester, username, email, phone, identityCheckService);
        this.user.updateProfile(requester, profile);

        this.operator = OperatorUser.requester(requester);
    }

    public void enable(Requester requester) {
        this.user.enable();

        this.operator = OperatorUser.requester(requester);
    }

    public void disable(Requester requester, AdministratorRootStrategy rootStrategy) {
        if (requester.isRequestUser(user.getUserId().getId())) {
            throw new IllegalArgumentException("Can not disable self.");
        }
        if (isRoot(rootStrategy)) {
            throw new IllegalArgumentException("Can not disable root administrator.");
        }

        this.user.disable();
        this.operator = OperatorUser.requester(requester);
    }

    public void resetPassword(Requester requester, PasswordService passwordService) {
        if (requester.isRequestUser(user.getUserId().getId())) {
            throw new IllegalArgumentException("Can not reset self.");
        }

        this.user.resetPassword(requester, passwordService);
        this.operator = OperatorUser.requester(requester);

        this.andEventsFrom(user);
    }

    public boolean isRoot(AdministratorRootStrategy rootStrategy) {
        return rootStrategy.isRootUser(this.getUser().getUserId());
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + administratorId +
                ", user=" + user +
                ", roles=" + roles +
                ", operator=" + operator +
                '}';
    }
}
