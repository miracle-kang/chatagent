package com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk;

/**
 * 消息内容
 */
public class TextMessageContent {
    private String content;

    public TextMessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
