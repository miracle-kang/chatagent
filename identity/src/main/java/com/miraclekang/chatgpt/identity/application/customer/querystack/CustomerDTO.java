package com.miraclekang.chatgpt.identity.application.customer.querystack;

import com.miraclekang.chatgpt.identity.application.user.querystack.UserEquityInfoDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import com.miraclekang.chatgpt.common.repo.SearchKeyMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CustomerDTO {

    @Schema(description = "Customer ID")
    private String customerId;
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

    @Schema(description = "Equities")
    private List<UserEquityInfoDTO> equities;

    @Schema(description = "Operator")
    private String operator;

    public CustomerDTO(String customerId, String userId, String username, String emailAddress, String phoneNumber,
                       Boolean disabled, UserProfileDTO profile, List<UserEquityInfoDTO> equities, String operator) {
        this.customerId = customerId;
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.disabled = disabled;
        this.profile = profile;
        this.equities = equities;
        this.operator = operator;
    }

    public static SearchKeyMapping searchKeyMapping = key -> switch (key) {
        case "emailAddress" -> "user.email.address";
        case "phoneNumber" -> "user.phone.number";
        case "disabled" -> "user.disabled";
        default -> key;
    };
}
