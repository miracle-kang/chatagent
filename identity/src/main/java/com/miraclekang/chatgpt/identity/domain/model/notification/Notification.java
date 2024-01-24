package com.miraclekang.chatgpt.identity.domain.model.notification;

import com.miraclekang.chatgpt.common.model.ValueObject;
import lombok.Getter;

@Getter
public class Notification extends ValueObject {

    private final String title;
    private final String content;

    public Notification(String content) {
        this(null, content);
    }

    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
