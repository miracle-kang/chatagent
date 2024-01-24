package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.common.access.EnumOperationAction;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OperationRegistry {

    // 系统支持的操作
    private final Map<String, Set<Operation>> operationMap = new LinkedHashMap<>();

    /**
     * 从应用加载操作列表
     */
    public void loadOperations(List<Operation> operations) {
        if (operations == null) {
            return;
        }
        operations.stream().sorted(Comparator.comparing(Operation::getKey)
                        .thenComparing(Operation::getAction))
                .forEach(operation -> {
                    Set<Operation> operationList = operationMap.computeIfAbsent(operation.getKey(), k -> new LinkedHashSet<>());
                    operationList.add(operation);
                });
    }

    /**
     * 获取系统支持的操作项列表
     */
    public List<Operation> supportedOperations() {
        return operationMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 通过key和action获取一个操作项
     */
    public Operation operation(String key, EnumOperationAction action) {
        Set<Operation> keyOperations = operationMap.get(key);
        if (keyOperations == null) {
            return null;
        }
        return keyOperations.stream().filter(operation -> operation.getAction() == action)
                .findFirst().orElse(null);
    }
}
