package com.miraclekang.chatgpt.assistant.domain.model.equity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Embeddable
public class EquityId {

    // @Comment("Equity Type")
    @Enumerated(EnumType.STRING)
    @Column(name = "equityType", length = 16, nullable = false)
    private String type;
    // @Comment("Equity ID")
    @Column(name = "equityId", length = 64, nullable = false)
    private String id;

    protected EquityId() {
    }

    public EquityId(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EquityId equityId = (EquityId) o;

        return new EqualsBuilder().append(type, equityId.type).append(id, equityId.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(type).append(id).toHashCode();
    }
}
