package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfo;
import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfoService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserInfo;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistration;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistrationRepository;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PublicUserInfoDTO {

    @Schema(description = "User ID")
    private String userId;
    @Schema(description = "Username")
    private String username;
    @Schema(description = "Email")
    private String email;
    @Schema(description = "Phone")
    private String phone;
    @Schema(description = "Role")
    private UserType role;

    @Schema(description = "Profile")
    private UserProfileDTO profile;

    @Schema(description = "Registrations")
    private List<UserRegistrationDTO> registrations;
    @Schema(description = "Equities")
    private List<UserEquityInfoDTO> equities;

    public PublicUserInfoDTO(String userId, String username, String email,
                             String phone, UserType role,
                             UserProfileDTO profile,
                             List<UserRegistrationDTO> registrations,
                             List<UserEquityInfoDTO> equities) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profile = profile;
        this.registrations = registrations;
        this.equities = equities;
    }

    public static PublicUserInfoDTO from(UserInfo userInfo) {
        return from(userInfo, null, null);
    }

    public static PublicUserInfoDTO from(UserInfo userInfo, UserRegistrationRepository registrationRepository,
                                         UserEquityInfoService equityInfoService) {
        List<UserRegistration> userRegistrations = List.of();
        List<UserEquityInfo> userEquityInfos = List.of();
        if (registrationRepository != null)
            userRegistrations = registrationRepository.findByUserId(userInfo.getUserId());
        if (equityInfoService != null)
            userEquityInfos = equityInfoService.userEquities(userInfo.getUserId());

        return new PublicUserInfoDTO(
                userInfo.getUserId().getId(),
                userInfo.getUsername().getUsername(),
                userInfo.getEmail() == null ? null : userInfo.getEmail().getAddress(),
                userInfo.getPhone() == null ? null : userInfo.getPhone().fullProtectedNumber(),
                userInfo.getRole(),
                UserProfileDTO.from(userInfo.getProfile()),
                userRegistrations.stream().map(UserRegistrationDTO::from).toList(),
                userEquityInfos.stream().map(UserEquityInfoDTO::from).toList()
        );
    }
}
