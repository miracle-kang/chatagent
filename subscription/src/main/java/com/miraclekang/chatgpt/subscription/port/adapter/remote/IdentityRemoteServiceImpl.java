package com.miraclekang.chatgpt.subscription.port.adapter.remote;

import com.miraclekang.chatgpt.common.facade.IdentityServiceFacade;
import com.miraclekang.chatgpt.common.facade.dto.UserInfoDTO;
import com.miraclekang.chatgpt.subscription.domain.model.identity.*;
import org.springframework.stereotype.Component;

@Component
public class IdentityRemoteServiceImpl implements UserInfoService {

    private final IdentityServiceFacade identityServiceFacade;

    public IdentityRemoteServiceImpl(IdentityServiceFacade identityServiceFacade) {
        this.identityServiceFacade = identityServiceFacade;
    }

    @Override
    public UserInfo userInfo(UserId userId) {
        UserInfoDTO userInfoDTO = identityServiceFacade.userInfo(userId.getId());
        if (userInfoDTO == null) {
            return null;
        }
        return new UserInfo(
                new UserId(userInfoDTO.getUserId()),
                userInfoDTO.getUsername(),
                userInfoDTO.getEmail(),
                userInfoDTO.getPhone(),
                UserType.valueOf(userInfoDTO.getRole()),
                new UserProfile(
                        userInfoDTO.getProfile().getNickname(),
                        userInfoDTO.getProfile().getGender(),
                        userInfoDTO.getProfile().getAvatarUrl(),
                        userInfoDTO.getProfile().getDescription(),
                        userInfoDTO.getProfile().getSettings()
                )
        );
    }

    @Override
    public String username(UserId userId) {
        return identityServiceFacade.username(userId.getId());
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return identityServiceFacade.existsUser(userId.getId());
    }
}
