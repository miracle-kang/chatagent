
package com.miraclekang.chatgpt.identity.application.common.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateSystemProfileCommand {

    @Schema(description = "name")
    private String name;
    @Schema(description = "announcement")
    private String announcement;
    @Schema(description = "wechat group QR light image file id")
    private String wechatGroupQRLightImageFileId;
    @Schema(description = "wechat group QR dark image file id")
    private String wechatGroupQRDarkImageFileId;
}
