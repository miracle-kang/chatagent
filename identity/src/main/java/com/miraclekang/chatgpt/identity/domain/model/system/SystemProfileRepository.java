package com.miraclekang.chatgpt.identity.domain.model.system;

import org.springframework.data.repository.CrudRepository;

public interface SystemProfileRepository extends CrudRepository<SystemProfile, Long> {

    default SystemProfile systemConfig() {
        Iterable<SystemProfile> iterable = findAll();
        if (iterable.iterator().hasNext()) {
            return iterable.iterator().next();
        } else {
            return SystemProfile.defaultSystemConfig();
        }
    }
}
