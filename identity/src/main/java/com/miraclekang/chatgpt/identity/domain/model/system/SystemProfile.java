package com.miraclekang.chatgpt.identity.domain.model.system;

import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.identity.domain.model.file.FileReference;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class SystemProfile extends BaseEntity {

    // @Comment("Name")
    private String name;

    // @Comment("announcement")
    @Column(columnDefinition = "TEXT")
    private String announcement;

    // @Comment("Wechat Group QR light image File")
    @AttributeOverride(name = "fileId", column = @Column(name = "wechatGroupQRLightImageId", length = 64))
    private FileReference wechatGroupQRLightImage;
    // @Comment("Wechat Group QR dark image File")
    @AttributeOverride(name = "fileId", column = @Column(name = "wechatGroupQRDarkImageId", length = 64))
    private FileReference wechatGroupQRDarkImage;

    public SystemProfile(String name, String announcement,
                         FileReference wechatGroupQRLightImage,
                         FileReference wechatGroupQRDarkImage) {
        this.name = name;
        this.announcement = announcement;

        if (wechatGroupQRLightImage != null) {
            this.wechatGroupQRLightImage = wechatGroupQRLightImage;
            registerEvent(this.wechatGroupQRLightImage.bind());
        }
        if (wechatGroupQRDarkImage != null) {
            this.wechatGroupQRDarkImage = wechatGroupQRDarkImage;
            registerEvent(this.wechatGroupQRDarkImage.bind());
        }
    }

    public void update(String name, String announcement,
                       FileReference wechatGroupQRLightImage,
                       FileReference wechatGroupQRDarkImage) {
        this.name = name;
        this.announcement = announcement;

        if (wechatGroupQRLightImage != null && !Objects.equals(this.wechatGroupQRLightImage, wechatGroupQRLightImage)) {
            // release old file reference
            if (this.wechatGroupQRLightImage != null) {
                registerEvent(this.wechatGroupQRLightImage.release());
            }
            // bind new file reference
            this.wechatGroupQRLightImage = wechatGroupQRLightImage;
            registerEvent(this.wechatGroupQRLightImage.bind());
        }
        if (wechatGroupQRDarkImage != null && !Objects.equals(this.wechatGroupQRDarkImage, wechatGroupQRDarkImage)) {
            // release old file reference
            if (this.wechatGroupQRDarkImage != null) {
                registerEvent(this.wechatGroupQRDarkImage.release());
            }
            // bind new file reference
            this.wechatGroupQRDarkImage = wechatGroupQRDarkImage;
            registerEvent(this.wechatGroupQRDarkImage.bind());
        }
    }

    public static SystemProfile defaultSystemConfig() {
        return new SystemProfile("春蚕AI", "Welcome to 春蚕AI", null, null);
    }
}
