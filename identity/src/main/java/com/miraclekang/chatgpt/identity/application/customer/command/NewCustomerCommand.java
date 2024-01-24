package com.miraclekang.chatgpt.identity.application.customer.command;

import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NewCustomerCommand {

    @Schema(description = "Username")
    private String username;
    @Schema(description = "Email")
    private String emailAddress;
    @Schema(description = "Phone")
    private String phoneNumber;
    @Schema(description = "Disabled")
    private Boolean disabled;
    @Schema(description = "Password")
    private String password;

    @Schema(description = "Profile")
    private UserProfileDTO profile;
}
