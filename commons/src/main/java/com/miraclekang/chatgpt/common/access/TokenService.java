package com.miraclekang.chatgpt.common.access;


public interface TokenService {

    /**
     * 生成Token
     *
     * @param userDescriptor 用户描述信息
     * @return Token
     */
    AuthenticateToken generateToken(UserPrincipal userDescriptor);

    /**
     * 解析用户Token
     *
     * @param token Token
     * @return 解析后的用户描述
     */
    UserPrincipal decodeToken(String token);
}
