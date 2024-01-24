package com.miraclekang.chatgpt.common.facade.dto;

import lombok.Data;

@Data
public class UserInfoDTO {

    private String userId;
    private String username;
    private String email;
    private String phone;
    private String role;

    private UserProfileDTO profile;
}
