package com.miraclekang.chatgpt.common.facade.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UserProfileDTO {

    private String nickname;
    private String gender;
    private String avatarUrl;
    private String description;

    private Map<String, String> settings;
}
