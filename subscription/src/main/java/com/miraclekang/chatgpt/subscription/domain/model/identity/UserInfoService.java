package com.miraclekang.chatgpt.subscription.domain.model.identity;

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

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(UserId userId);
}
