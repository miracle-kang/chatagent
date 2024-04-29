package com.miraclekang.chatgpt.agent.domain.model.identity;

import reactor.core.publisher.Mono;

public interface UserInfoService {

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Mono<UserInfo> userInfo(UserId userId);

    /**
     * 获取用户名
     *
     * @param userId 用户ID
     * @return 用户名
     */
    Mono<String> username(UserId userId);
}
