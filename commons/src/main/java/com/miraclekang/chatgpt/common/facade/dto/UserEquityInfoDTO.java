package com.miraclekang.chatgpt.common.facade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class UserEquityInfoDTO {

    private String userEquityId;
    private String equityType;
    private String equityId;

    private String equityName;
    private BigDecimal quantity;
    private String unit;

    private ZonedDateTime effectiveTime;
    private ZonedDateTime expiresTime;
    private String status;

    private EquityLimitationDTO limitation;
}
