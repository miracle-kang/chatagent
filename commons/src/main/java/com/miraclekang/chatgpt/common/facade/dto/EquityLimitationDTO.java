package com.miraclekang.chatgpt.common.facade.dto;

import lombok.Data;

import java.util.List;

@Data
public class EquityLimitationDTO {
    private Boolean effective;
    private Long maxTokensPerDay;
    private Long maxTokensPerMonth;
    private Long maxTokensPerRequest;
    private List<String> chatModels;
}
