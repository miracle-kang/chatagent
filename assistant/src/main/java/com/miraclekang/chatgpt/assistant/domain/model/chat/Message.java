package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.assistant.domain.model.billing.Token;
import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
@Embeddable
public class Message extends ValueObject {

    // @Comment("Chat message role")
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private MessageRole role;

    // @Comment("Chat message content")
    @Column(columnDefinition = "TEXT")
    private String content;

    protected Message() {
    }

    private Message(MessageRole role, String content) {
        this.role = role;
        this.content = content;
    }

    /**
     * 计算Token用量
     */
    public Token encodeToken(ChatModel model) {
        return Token.encodeMessage(model.getId(), this);
    }

    public ChatMessage toChatMessage() {
        return new ChatMessage(role.value(), content);
    }

    public static Message ofSystem(String content) {
        return new Message(MessageRole.SYSTEM, content);
    }

    public static Message ofUser(String content) {
        return new Message(MessageRole.USER, content);
    }

    public static Message ofAssistant(String content) {
        return new Message(MessageRole.ASSISTANT, content);
    }

    public static Message ofAssistant(List<Message> messagePartials) {
        return new Message(
                MessageRole.ASSISTANT,
                messagePartials.stream()
                        .map(Message::getContent)
                        .filter(Objects::nonNull)
                        .reduce((s1, s2) -> s1 + s2)
                        .orElse("")
        );
    }

    public static Message of(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }

        return new Message(MessageRole.of(chatMessage.getRole()), chatMessage.getContent());
    }

    @Override
    public String toString() {
        return "Message{" +
                "role=" + role +
                ", content='" + content + '\'' +
                '}';
    }
}
