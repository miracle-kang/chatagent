package com.miraclekang.chatgpt.identity.domain.model.identity;

public interface VerificationService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    String sendVerificationCode(Phone phone, VerificationType type);

    /**
     * 发送邮件验证码
     *
     * @param email 邮箱地址
     * @return 验证码
     */
    String sendVerificationCode(Email email, VerificationType type);

    /**
     * 校验验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    Boolean checkVerificationCode(Phone phone, String code);

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱地址
     * @param code  验证码
     */
    Boolean checkVerificationCode(Email email, String code);
}
