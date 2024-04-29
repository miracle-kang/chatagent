package com.miraclekang.chatgpt.agent.port.adapter.restapi.open.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EnableRequest {

    @Schema(description = "Enable or disable")
    private Boolean enabled;
}
