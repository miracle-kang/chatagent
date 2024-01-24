package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.common.access.EnumOperationAction;
import com.miraclekang.chatgpt.common.model.ValueObject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.Objects;

/**
 * 操作权限
 */
@Getter
@Embeddable
public class Permission extends ValueObject {

    // @Comment("Operation Key")
    @Column(name = "operationKey", length = 128, nullable = false)
    private String operationKey;

    // @Comment("Operation Action")
    @Enumerated(EnumType.STRING)
    @Column(name = "operationAction", length = 6, nullable = false)
    private EnumOperationAction operationAction;

    protected Permission() {
    }

    public Permission(String operationKey, EnumOperationAction operationAction) {
        Validate.notBlank(operationKey, "Operation key must not be null.");
        Validate.notNull(operationAction, "Operation action must not be null.");

        this.operationKey = operationKey;
        this.operationAction = operationAction;
    }

    public Permission(String uniqueKey) {
        Validate.notBlank(uniqueKey, "Operation unique key must not be null.");

        String[] actionKey = uniqueKey.split(":");
        if (actionKey.length != 2) {
            throw new IllegalArgumentException("Illegal operation unique key " + uniqueKey);
        }
        this.operationAction = EnumOperationAction.forName(actionKey[0]);
        if (operationAction == null) {
            throw new IllegalArgumentException("Illegal operation action " + actionKey[0]);
        }
        this.operationKey = actionKey[1];
    }

    public String uniqueKey() {
        return operationAction.name() + ":" + operationKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(operationKey, that.operationKey) && operationAction == that.operationAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationKey, operationAction);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "operationKey='" + operationKey + '\'' +
                ", operationAction=" + operationAction +
                '}';
    }
}
