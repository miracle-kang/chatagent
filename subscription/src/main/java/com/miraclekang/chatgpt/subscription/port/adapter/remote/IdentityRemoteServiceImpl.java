package com.miraclekang.chatgpt.subscription.port.adapter.remote;

import com.miraclekang.chatgpt.common.facade.IdentityServiceFacade;
import com.miraclekang.chatgpt.common.facade.ReactiveAdapterInterceptor;
import com.miraclekang.chatgpt.subscription.domain.model.identity.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Component
public class IdentityRemoteServiceImpl implements UserInfoService {

    private final IdentityServiceFacade identityServiceFacade;

    public IdentityRemoteServiceImpl(IdentityServiceFacade identityServiceFacade) {
        this.identityServiceFacade = identityServiceFacade;
    }

    @Override
    public Mono<UserInfo> userInfo(UserId userId) {
        return Mono.just(userId.getId())
                .flatMap(uid -> blockingOperation(() -> identityServiceFacade.userInfo(uid)))
                .mapNotNull(optional -> optional.orElse(null))
                .map(userInfoDTO -> new UserInfo(
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
                ));
    }

    @Override
    public Mono<String> username(UserId userId) {
        return Mono.just(userId.getId())
                .flatMap(uid -> blockingOperation(() -> identityServiceFacade.username(uid)))
                .mapNotNull(optional -> optional.orElse(null));
    }

    @Override
    public Mono<Boolean> existsByUserId(UserId userId) {
        return Mono.just(userId.getId())
                .flatMap(uid -> blockingOperation(() -> identityServiceFacade.existsUser(uid)))
                .mapNotNull(optional -> optional.orElse(null));
    }
}
