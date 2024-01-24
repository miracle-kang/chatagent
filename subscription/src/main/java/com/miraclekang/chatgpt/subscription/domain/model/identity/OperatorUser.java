package com.miraclekang.chatgpt.subscription.domain.model.identity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.common.reactive.Requester;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OperatorUser extends ValueObject {

    private static final UserId SYSTEM_USER_ID = new UserId("0");

    @AttributeOverride(name = "id", column = @Column(name = "operatorUserId", length = 64))
    private UserId userId;

    @Column(name = "operatorUserName", length = 128)
    private String userName;

    @Column(name = "operatorUserIp", length = 64)
    private String userIp;

    OperatorUser(UserId userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    OperatorUser(UserId userId, String userName, String userIp) {
        this.userId = userId;
        this.userName = userName;
        this.userIp = userIp;
    }

    /**
     * 获取当前操作用户
     *
     * @return 操作用户
     */
    public static OperatorUser requester(Requester requester) {
        if (requester == null || !requester.isAuthenticated()) {
            return null;
        }
        String currentUsername;
        if (requester.isCustomer()) {
            currentUsername = "Customer";
        } else {
            currentUsername = requester.getUsername();
        }
        return new OperatorUser(
                new UserId(requester.getUserId()),
                currentUsername,
                requester.getIp()
        );
    }

    /**
     * 获取当前操作用户，如果当前用户未登录返回系统
     */
    public static OperatorUser currentOrSystemOperator(Requester requester) {
        if (!requester.isAuthenticated()) {
            return systemOperator();
        }
        return requester(requester);
    }

    /**
     * 系统
     */
    public static OperatorUser systemOperator() {
        return new OperatorUser(
                SYSTEM_USER_ID,
                "System"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperatorUser that = (OperatorUser) o;
        return Objects.equals(userId, that.userId) && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName);
    }

    @Override
    public String toString() {
        return "OperatorUser{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
