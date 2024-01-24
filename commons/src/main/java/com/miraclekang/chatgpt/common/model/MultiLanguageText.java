package com.miraclekang.chatgpt.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class MultiLanguageText extends ValueObject {

    // @Comment("Simple Chinese")
    @Column(length = 256)
    @Schema(description = "简体中文")
    private String sc;

    // @Comment("Traditional Chinese")
    @Column(length = 256)
    @Schema(description = "繁体中文")
    private String tc;

    // @Comment("English")
    @Column(length = 512)
    @Schema(description = "美式英文")
    private String us;

    protected MultiLanguageText() {
    }

    public MultiLanguageText(String sc, String tc, String us) {
        this.sc = sc;
        this.tc = tc;
        this.us = us;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiLanguageText that = (MultiLanguageText) o;
        return Objects.equals(sc, that.sc) && Objects.equals(tc, that.tc) && Objects.equals(us, that.us);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sc, tc, us);
    }

    @Override
    public String toString() {
        return "MultiLanguageText{" +
                "sc='" + sc + '\'' +
                ", tc='" + tc + '\'' +
                ", us='" + us + '\'' +
                '}';
    }
}
