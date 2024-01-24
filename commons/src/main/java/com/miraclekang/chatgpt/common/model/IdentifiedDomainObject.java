package com.miraclekang.chatgpt.common.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.io.Serializable;

@MappedSuperclass
public class IdentifiedDomainObject extends AbstractAggregateRoot<IdentifiedDomainObject> implements Serializable {

    @Id
    // @Comment("Primary Key")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected void setId(Long anId) {
        this.id = anId;
    }

    protected Long getId() {
        return id;
    }
}
