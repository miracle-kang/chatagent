package com.miraclekang.chatgpt.identity.application.administrator.querystack;

import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import com.miraclekang.chatgpt.identity.domain.model.identity.Administrator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AdministratorDTO {

    @Schema(description = "Administrator ID")
    private String administratorId;
    @Schema(description = "User ID")
    private String userId;
    @Schema(description = "Username")
    private String username;
    @Schema(description = "Email")
    private String emailAddress;
    @Schema(description = "Phone")
    private String phoneNumber;
    @Schema(description = "Disabled")
    private Boolean disabled;

    @Schema(description = "Profile")
    private UserProfileDTO profile;

    @Schema(description = "Roles")
    private List<RoleDTO> roles;

    @Schema(description = "Operator")
    private String operator;

    public AdministratorDTO(String administratorId, String userId, String username, String emailAddress, String phoneNumber,
                            Boolean disabled, UserProfileDTO profile, List<RoleDTO> roles, String operator) {
        this.administratorId = administratorId;
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.disabled = disabled;
        this.profile = profile;
        this.roles = roles;
        this.operator = operator;
    }

    public static AdministratorDTO from(Administrator administrator) {
        if (administrator == null) {
            return new AdministratorDTO();
        }
        return new AdministratorDTO(
                administrator.getAdministratorId().getId(),
                administrator.getUser().getUserId().getId(),
                administrator.getUser().getUsername().getUsername(),
                administrator.getUser().getEmail() == null ? null : administrator.getUser().getEmail().getAddress(),
                administrator.getUser().getPhone() == null ? null : administrator.getUser().getPhone().getNumber(),
                administrator.getUser().getDisabled(),
                UserProfileDTO.from(administrator.getUser().getProfile()),
                administrator.getRoles().stream().map(RoleDTO::from).toList(),
                administrator.getOperator() == null ? null : administrator.getOperator().getUserName()
        );
    }
}
