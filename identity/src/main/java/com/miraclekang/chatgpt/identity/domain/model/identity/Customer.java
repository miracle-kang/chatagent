package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.exception.DomainException;
import com.miraclekang.chatgpt.identity.domain.EnumExceptionCode;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "UK_customer_id", columnList = "customerId", unique = true)
})
public class Customer extends AggregateRoot {

    // @Comment("Customer ID")
    @AttributeOverride(name = "id", column = @Column(name = "customerId", length = 64, nullable = false))
    private CustomerId customerId;

    // @Comment("Owner User")
    @JoinColumn(name = "userId")
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @Embedded
    // @Comment("Operator")
    @AttributeOverrides( value = {
            @AttributeOverride(name = "userId", column = @Column(name = "operatorUserId", length = 64)),
            @AttributeOverride(name = "userName", column = @Column(name = "operatorUserName", length = 128)),
            @AttributeOverride(name = "userIp", column = @Column(name = "operatorUserIp", length = 64))
    })
    private OperatorUser operator;

    public Customer(CustomerId customerId, User user, Requester requester) {
        this.customerId = customerId;
        this.user = user;
        this.operator = OperatorUser.requester(requester);

        this.andEventsFrom(user);
    }

    public Customer(CustomerId customerId, User user) {
        this.customerId = customerId;
        this.user = user;

        this.andEventsFrom(user);
    }

    public void update(Username username, Email email, Phone phone, UserProfile profile,
                       Requester requester, UserIdentityCheckService identityCheckService) {
        if (!requester.isAdmin() && !requester.isRequestUser(user.getUserId().getId())) {
            throw new DomainException("Can not update other user.", EnumExceptionCode.Forbidden);
        }

        this.user.update(requester, username, email, phone, identityCheckService);
        this.user.updateProfile(requester, profile);

        this.operator = OperatorUser.requester(requester);
    }

    public void enable(Requester requester) {
        if (!requester.isAdmin() && !requester.isRequestUser(user.getUserId().getId())) {
            throw new DomainException("Can not update other user.", EnumExceptionCode.Forbidden);
        }
        this.user.enable();

        this.operator = OperatorUser.requester(requester);
    }

    public void disable(Requester requester) {
        if (!requester.isAdmin() && !requester.isRequestUser(user.getUserId().getId())) {
            throw new DomainException("Can not update other user.", EnumExceptionCode.Forbidden);
        }
        if (requester.isRequestUser(user.getUserId().getId())) {
            throw new IllegalArgumentException("Can not disable self.");
        }
        this.user.disable();
        this.operator = OperatorUser.requester(requester);
    }

    public void resetPassword(Requester requester, PasswordService passwordService) {
        this.user.resetPassword(requester, passwordService);
        this.operator = OperatorUser.requester(requester);

        this.andEventsFrom(user);
    }
}
