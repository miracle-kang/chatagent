package com.miraclekang.chatgpt.identity.domain.model.identity;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.identity.domain.model.access.RoleId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Embeddable
@NoArgsConstructor
public class AdministratorRole extends ValueObject {

    @Embedded
    // @Comment("Role ID")
    @AttributeOverride(name = "id", column = @Column(name = "roleId", length = 64, nullable = false))
    private RoleId roleId;

    // @Comment("Role name")
    @Column(name = "roleName")
    private String roleName;

    public AdministratorRole(RoleId roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AdministratorRole administratorRole = (AdministratorRole) o;

        return new EqualsBuilder().append(roleId, administratorRole.roleId).isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37).append(roleId).toHashCode();
    }

    @Override public String toString() {
        return "AdminRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
