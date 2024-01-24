package com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Specified here
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextMessage implements IDingTalkMessage {

    private TextMessageContent text;
    private MessageAt at;

    public TextMessage(String message) {
        this.text = new TextMessageContent(message);
    }

    public TextMessage(String message, MessageAt at) {
        this.text = new TextMessageContent(message);
        this.at = at;
    }

    @Override
    public String getMsgType() {
        return "text";
    }

    public TextMessageContent getText() {
        return text;
    }

    public MessageAt getAt() {
        return at;
    }
}
