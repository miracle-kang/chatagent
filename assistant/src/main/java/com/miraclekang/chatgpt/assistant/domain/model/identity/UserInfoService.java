package com.miraclekang.chatgpt.assistant.domain.model.identity;

public interface UserInfoService {

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo userInfo(UserId userId);

    /**
     * 获取用户名
     *
     * @param userId 用户ID
     * @return 用户名
     */
    String username(UserId userId);
}
