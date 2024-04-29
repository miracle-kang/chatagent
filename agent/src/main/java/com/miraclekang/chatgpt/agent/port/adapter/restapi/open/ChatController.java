package com.miraclekang.chatgpt.agent.port.adapter.restapi.open;

import com.miraclekang.chatgpt.agent.application.chat.ChatService;
import com.miraclekang.chatgpt.agent.application.chat.command.NewConversationCommand;
import com.miraclekang.chatgpt.agent.application.chat.command.NewMessageCommand;
import com.miraclekang.chatgpt.agent.application.chat.querystack.ConversationDTO;
import com.miraclekang.chatgpt.agent.application.chat.querystack.MessageDTO;
import com.miraclekang.chatgpt.agent.application.chat.querystack.MessagePartialDTO;
import com.miraclekang.chatgpt.common.restapi.NoBodyWarp;
import com.miraclekang.chatgpt.agent.port.adapter.restapi.open.dto.EnableRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/open/chat")
@Tag(name = "Chat APIs", description = "Chat APIs")
public class ChatController {

    private final ChatService conversationService;

    public ChatController(ChatService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/conversations")
    @Operation(summary = "New conversation", description = "Create a new conversation")
    public Mono<ConversationDTO> newConversation(@RequestBody NewConversationCommand command) {
        return conversationService.newConversation(command);
    }

    @PutMapping("/conversations/{conversationId}")
    @Operation(summary = "Update conversation", description = "Update a conversation")
    public Mono<ConversationDTO> updateConversation(@PathVariable String conversationId,
                                                    @RequestBody NewConversationCommand command) {
        return conversationService.updateConversation(conversationId, command);
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get conversation", description = "Get a conversation")
    public Mono<ConversationDTO> getConversation(@PathVariable String conversationId) {
        return conversationService.getConversation(conversationId);
    }

    @PutMapping("/conversations/{conversationId}/send-history")
    @Operation(summary = "Enable send history", description = "Enable send history for a conversation")
    public Mono<ConversationDTO> enabledSendHistory(@PathVariable String conversationId,
                                                    @RequestBody EnableRequest request) {
        return conversationService.enabledConversationSendHistory(conversationId, request.getEnabled());
    }

    @DeleteMapping("/conversations/{conversationId}")
    @Operation(summary = "Delete conversation", description = "Delete a conversation")
    public Mono<Void> deleteConversation(@PathVariable String conversationId) {
        return conversationService.deleteConversation(conversationId);
    }

    @PageableAsQueryParam
    @GetMapping("/conversations")
    @Operation(summary = "Query conversations", description = "Query conversations")
    public Mono<Page<ConversationDTO>> queryConversations(@ParameterObject Pageable pageable) {
        return conversationService.queryConversations(pageable);
    }

    @NoBodyWarp
    @PostMapping(path = "/conversations/{conversationId}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "New conversation message", description = "New conversation message")
    public Flux<MessagePartialDTO> newConversationMessage(@PathVariable String conversationId,
                                                          @RequestBody NewMessageCommand command) {
        return conversationService.newConversationMessage(conversationId, command);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Query conversation messages", description = "Query conversation messages")
    public Mono<Page<MessageDTO>> queryConversationMessages(@PathVariable String conversationId,
                                                            @ParameterObject Pageable pageable) {
        return conversationService.queryConversationMessages(conversationId, pageable);
    }

    @DeleteMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Clear conversation messages", description = "Clear conversation messages")
    public Mono<Void> clearConversationMessages(@PathVariable String conversationId) {
        return conversationService.clearConversationMessages(conversationId);
    }

    @DeleteMapping("/conversations/{conversationId}/messages/{messageId}")
    @Operation(summary = "Delete conversation message", description = "Delete conversation message")
    public Mono<Void> deleteMessage(@PathVariable String conversationId, @PathVariable String messageId) {
        return conversationService.deleteMessage(conversationId, messageId);
    }

    @GetMapping("/conversations/archives")
    @Operation(summary = "Query conversation archives", description = "Query conversation archives")
    public Mono<Page<ConversationDTO>> queryConversationArchives(@ParameterObject Pageable pageable) {
        return conversationService.queryConversationArchives(pageable);
    }

    @GetMapping("/conversations/archives/{archiveId}/messages")
    @Operation(summary = "Query conversation archive messages", description = "Query conversation archive messages")
    public Mono<Page<MessageDTO>> queryConversationArchiveMessages(@PathVariable String archiveId,
                                                                   @ParameterObject Pageable pageable) {
        return conversationService.queryConversationArchiveMessages(archiveId, pageable);
    }

}
