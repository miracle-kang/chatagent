package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.common.access.EnumOperationAction;
import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

import java.util.Objects;

/**
 * 用户的一次操作
 */
@Getter
public class Operation extends ValueObject {

    private final String key;
    private final EnumOperationAction action;

    private final String name;
    private final String description;

    public Operation(String key, EnumOperationAction action, String name, String description) {
        this.key = key;
        this.action = action;
        this.name = name;
        this.description = description;
    }

    public String uniqueKey() {
        return action.name() + ":" + key;
    }

    public Permission operationPermission() {
        return new Permission(key, action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(key, operation.key) && action == operation.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, action);
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operationKey='" + key + '\'' +
                ", operationType=" + action +
                '}';
    }
}
