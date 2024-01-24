package com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specified here
 */
public interface IDingTalkMessage {

    @JsonProperty("msgtype")
    String getMsgType();
}
