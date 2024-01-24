package com.miraclekang.chatgpt.identity.application.administrator.querystack;

import com.miraclekang.chatgpt.identity.domain.model.identity.AdministratorRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleDTO {

    private String roleId;
    private String roleName;

    public RoleDTO(String roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public static RoleDTO from(AdministratorRole role) {
        return new RoleDTO(
                role.getRoleId().getId(),
                role.getRoleName()
        );
    }
}
