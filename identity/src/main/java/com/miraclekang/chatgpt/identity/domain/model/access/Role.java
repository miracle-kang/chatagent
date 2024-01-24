package com.miraclekang.chatgpt.identity.domain.model.access;

import com.miraclekang.chatgpt.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "UK_role_id", columnList = "roleId")
})
public class Role extends BaseEntity {

    @Embedded
    // @Comment("Role ID")
    @AttributeOverride(name = "id", column = @Column(name = "roleId", length = 64, nullable = false))
    private RoleId roleId;

    // @Comment("Role name")
    private String name;

    // @Comment("Role description")
    private String description;

    @Getter(AccessLevel.PRIVATE)
    // @Comment("Role permissions")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rolePermission", joinColumns = {
            @JoinColumn(name = "roleId")
    })
    private final Set<Permission> permissions = new LinkedHashSet<>();

    public Role(RoleId roleId, String name, String description) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
    }

    public void assignPermissions(List<Permission> permissionList) {
        this.permissions.addAll(permissionList);
    }
}
