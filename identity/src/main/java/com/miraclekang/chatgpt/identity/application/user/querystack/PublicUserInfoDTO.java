package com.miraclekang.chatgpt.identity.application.user.querystack;

import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfo;
import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfoService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserInfo;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistration;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistrationRepository;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

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

    public static Mono<PublicUserInfoDTO> from(UserInfo userInfo) {
        return from(userInfo, null, null);
    }

    public static Mono<PublicUserInfoDTO> from(UserInfo userInfo, UserRegistrationRepository registrationRepository,
                                               UserEquityInfoService equityInfoService) {

        return Mono.just(userInfo.getUserId())
                .flatMap(userId -> {
                    Mono<List<UserRegistration>> userRegistrations;
                    if (registrationRepository != null) {
                        userRegistrations = blockingOperation(() -> registrationRepository.findByUserId(userId));
                    } else {
                        userRegistrations = Mono.just(List.of());
                    }

                    Mono<List<UserEquityInfo>> userEquityInfos;
                    if (equityInfoService != null) {
                        userEquityInfos = equityInfoService.userEquities(userId).collectList();
                    } else {
                        userEquityInfos = Mono.just(List.of());
                    }
                    return Mono.zip(userRegistrations, userEquityInfos);
                })
                .map(tuple -> new PublicUserInfoDTO(
                        userInfo.getUserId().getId(),
                        userInfo.getUsername().getUsername(),
                        userInfo.getEmail() == null ? null : userInfo.getEmail().getAddress(),
                        userInfo.getPhone() == null ? null : userInfo.getPhone().fullProtectedNumber(),
                        userInfo.getRole(),
                        UserProfileDTO.from(userInfo.getProfile()),
                        tuple.getT1().stream().map(UserRegistrationDTO::from).toList(),
                        tuple.getT2().stream().map(UserEquityInfoDTO::from).toList()
                ));
    }
}
