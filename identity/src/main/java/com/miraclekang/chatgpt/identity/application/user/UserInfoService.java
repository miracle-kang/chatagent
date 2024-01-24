package com.miraclekang.chatgpt.identity.application.user;

import com.miraclekang.chatgpt.identity.application.user.querystack.UserInfoDTO;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserId;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.miraclekang.chatgpt.common.reactive.ReactiveUtils.blockingOperation;

@Service
public class UserInfoService {

    private final UserRepository userRepository;

    public UserInfoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserInfoDTO> userInfo(String anUserId) {
        Validate.notBlank(anUserId, "User ID must be provided");
        UserId userId = new UserId(anUserId);

        return blockingOperation(() -> userRepository.findByUserId(userId))
                .switchIfEmpty(Mono.error(new IllegalStateException("User not exists.")))
                .map(user -> UserInfoDTO.from(user.userInfo()));
    }

    public Mono<String> username(String anUserId) {
        Validate.notBlank(anUserId, "User ID must be provided");
        UserId userId = new UserId(anUserId);

        return blockingOperation(() -> userRepository.findByUserId(userId))
                .switchIfEmpty(Mono.error(new IllegalStateException("User not exists.")))
                .map(user -> user.getUsername().getUsername());
    }

    public Mono<Boolean> existsUser(String anUserId) {
        Validate.notBlank(anUserId, "User ID must be provided");
        UserId userId = new UserId(anUserId);

        return blockingOperation(() -> userRepository.existsByUserId(userId));
    }
}
