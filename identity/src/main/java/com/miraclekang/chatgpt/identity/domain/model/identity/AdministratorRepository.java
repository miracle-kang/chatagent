package com.miraclekang.chatgpt.identity.domain.model.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdministratorRepository extends JpaRepository<Administrator, Long>,
        JpaSpecificationExecutor<Administrator> {

    Administrator findByAdministratorId(AdministratorId administratorId);
}
