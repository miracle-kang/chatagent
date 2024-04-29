package com.miraclekang.chatgpt.agent.domain.model.chat;

import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    default Message sendMessage(Message message, MessageConfig config) {
        return sendMessages(List.of(message), config);
    }

    /**
     * Send messages and receive reply
     *
     * @param messages Message list
     * @return Reply message
     */
    Message sendMessages(List<Message> messages, MessageConfig config);


    default Flux<Message> fluxSendMessage(Message message, MessageConfig config) {
        return fluxSendMessages(List.of(message), config);
    }

    /**
     * Send conversation messages and receive part message flux
     *
     * @param messages Message list
     * @return Flux reply part message
     */
    Flux<Message> fluxSendMessages(List<Message> messages, MessageConfig config);
}
