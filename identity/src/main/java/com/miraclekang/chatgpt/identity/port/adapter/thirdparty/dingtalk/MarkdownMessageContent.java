package com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk;

public class MarkdownMessageContent {

    private String title;
    private String text;

    public MarkdownMessageContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
