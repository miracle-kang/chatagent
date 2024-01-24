package com.miraclekang.chatgpt.identity.application.common.querystack;

import com.miraclekang.chatgpt.identity.domain.model.file.FilePreviewUrl;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SystemProfileDTO {

    private String name;
    private String announcement;
    private FilePreviewUrl wechatGroupQRLightImage;
    private FilePreviewUrl wechatGroupQRDarkImage;

    public SystemProfileDTO(String name, String announcement,
                            FilePreviewUrl wechatGroupQRLightImage,
                            FilePreviewUrl wechatGroupQRDarkImage) {
        this.name = name;
        this.announcement = announcement;
        this.wechatGroupQRLightImage = wechatGroupQRLightImage;
        this.wechatGroupQRDarkImage = wechatGroupQRDarkImage;
    }
}
