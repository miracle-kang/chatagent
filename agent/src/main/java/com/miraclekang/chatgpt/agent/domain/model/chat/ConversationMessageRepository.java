package com.miraclekang.chatgpt.agent.domain.model.chat;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Comparator;
import java.util.List;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long>,
        JpaSpecificationExecutor<ConversationMessage> {

    default List<ConversationMessage> head(ConversationId conversationId, int lines) {
        return head(conversationId, lines, null);
    }

    default List<ConversationMessage> head(ConversationId conversationId, int lines, MessageStatus status) {
        Specification<ConversationMessage> conversationSpec = (root, query, builder) ->
                builder.equal(root.get(ConversationMessage_.conversationId), conversationId);

        if (status != null) {
            conversationSpec = conversationSpec.and((root, query, builder) ->
                    builder.equal(root.get(ConversationMessage_.status), status));
        }

        Pageable pageable = PageRequest.ofSize(lines);

        return findAll(conversationSpec, pageable).toList();
    }


    default List<ConversationMessage> tail(ConversationId conversationId, int lines) {
        return tail(conversationId, lines, null);
    }

    default List<ConversationMessage> tail(ConversationId conversationId, int lines, MessageStatus status) {
        Specification<ConversationMessage> conversationSpec = (root, query, builder) ->
                builder.equal(root.get(ConversationMessage_.conversationId), conversationId);

        if (status != null) {
            conversationSpec = conversationSpec.and((root, query, builder) ->
                    builder.equal(root.get(ConversationMessage_.status), status));
        }
        Pageable pageable = PageRequest.ofSize(lines)
                .withSort(Sort.by(ConversationMessage_.ID).descending());

        return findAll(conversationSpec, pageable).stream()
                .sorted(Comparator.comparing(ConversationMessage::dbId))
                .toList();
    }

    default void clearMessages(ConversationId conversationId) {
        Specification<ConversationMessage> conversationSpec = (root, query, builder) ->
                builder.equal(root.get(ConversationMessage_.conversationId), conversationId);

        delete(conversationSpec);
    }

    default ConversationMessage lastMessage(ConversationId conversationId, MessageRole role) {
        Specification<ConversationMessage> conversationSpec = (root, query, builder) ->
                builder.equal(root.get(ConversationMessage_.conversationId), conversationId);

        if (role != null) {
            conversationSpec = conversationSpec.and((root, query, builder) -> {
                var roleSpec = root.join(ConversationMessage_.message)
                        .get(Message_.role);
                return builder.equal(roleSpec, role);
            });
        }
        Pageable pageable = PageRequest.ofSize(1)
                .withSort(Sort.by(ConversationMessage_.ID).descending());

        return findAll(conversationSpec, pageable).stream()
                .findFirst()
                .orElse(null);
    }

    default ConversationMessage conversationMessage(ConversationId conversationId, ConversationMessageId messageId) {
        Specification<ConversationMessage> querySpec = (root, query, builder) ->
                builder.and(
                        builder.equal(root.get(ConversationMessage_.conversationId), conversationId),
                        builder.equal(root.get(ConversationMessage_.messageId), messageId)
                );

        return findOne(querySpec).orElse(null);
    }
}
