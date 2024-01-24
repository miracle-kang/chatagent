package com.miraclekang.chatgpt.identity.application.user;

import com.miraclekang.chatgpt.identity.application.user.command.UpdateUserProfileCommand;
import com.miraclekang.chatgpt.identity.application.user.querystack.PublicUserInfoDTO;
import com.miraclekang.chatgpt.identity.application.user.querystack.UserProfileDTO;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.identity.domain.model.equity.UserEquityInfoService;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRegistrationRepository registrationRepository;

    private final PasswordService passwordService;
    private final UserEquityInfoService equityInfoService;

    public UserService(UserRepository userRepository,
                       UserRegistrationRepository registrationRepository,
                       PasswordService passwordService,
                       UserEquityInfoService equityInfoService) {
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.equityInfoService = equityInfoService;
        this.passwordService = passwordService;
    }

    public Mono<PublicUserInfoDTO> currentUserInfo() {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> blockingOperation(() -> userRepository.findByUserId(new UserId(requester.getUserId())))
                        .switchIfEmpty(Mono.error(new IllegalStateException("User not exists.")))
                        .map(user -> PublicUserInfoDTO.from(user.userInfo(), registrationRepository, equityInfoService)));
    }

    public Mono<UserProfileDTO> currentUserProfile() {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .publishOn(Schedulers.boundedElastic())
                .mapNotNull(requester -> userRepository.findByUserId(new UserId(requester.getUserId())))
                .mapNotNull(user -> UserProfileDTO.from(user.getProfile()));
    }

    public Mono<UserProfileDTO> updateProfile(UpdateUserProfileCommand command) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new UserId(requester.getUserId()))
                        .map(userRepository::findByUserId)
                        .map(user -> user.updateProfile(requester, new UserProfile(
                                command.getNickname(),
                                command.getGender(),
                                command.getAvatarUrl(),
                                command.getDescription(),
                                command.getSettings()
                        ))))
                .map(UserProfileDTO::from);
    }

    public Mono<Void> changePassword(String password, String newPassword) {
        return Requester.currentRequester()
                .filter(Requester::isAuthenticated)
                .switchIfEmpty(Mono.error(new IllegalStateException("Requester is not authenticated")))
                .flatMap(requester -> Mono.just(new UserId(requester.getUserId()))
                        .mapNotNull(userRepository::findByUserId)
                        .flatMap(user -> {
                            user.changePassword(requester, password, newPassword, passwordService);
                            return blockingOperation(() -> userRepository.save(user));
                        }))
                .then();
    }
}
