package com.miraclekang.chatgpt.assistant.domain.model.chat;

import com.miraclekang.chatgpt.assistant.domain.model.equity.UserEquityChecker;
import com.miraclekang.chatgpt.assistant.domain.model.identity.UserId;
import com.miraclekang.chatgpt.common.model.AggregateRoot;
import com.miraclekang.chatgpt.common.model.IdentityGenerator;
import com.miraclekang.chatgpt.common.reactive.Requester;
import com.miraclekang.chatgpt.common.repo.Converters;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.validator.constraints.Range;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "UK_conversation_id", columnList = "conversationId", unique = true),
        @Index(name = "IDX_conversation_owner_user_id", columnList = "ownerUserId")
})
public class Conversation extends AggregateRoot {

    // @Comment("Conversation ID")
    @AttributeOverride(name = "id", column = @Column(name = "conversationId", length = 64, nullable = false))
    private ConversationId conversationId;
    // @Comment("Owner User")
    @AttributeOverride(name = "id", column = @Column(name = "ownerUserId", length = 64, nullable = false))
    private UserId ownerUserId;
    // @Comment("Conversation name")
    private String name;


    // Configuration for this conversation
    // @Comment("ID of the model to use.")
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ChatModel model;           // default gpt-3.5-turbo
    // @Comment("Whether to send the chat completion history API. (default true)")
    private Boolean sendHistory;    // default true

    // @Comment("Sampling temperature to use")
    @Range(min = 0, max = 2, message = "between 0 and 2.")
    private Double temperature;     // default 0.7
    // @Comment("Alternative to sampling with temperature")
    private Double topP;            // default 1
    // @Comment("The maximum number of tokens to generate in the chat completion.")
    private Integer maxTokens;      // default 2000

    // System configure
    // @Comment("System message of this conversation")
    private String systemMessage;  // default null

    // @Comment("Chat completion choices to generate")
    private Integer choices;        // default 1
    @Lob
    // @Comment("Up to 4 sequences where the API will stop generating further tokens.")
    @Convert(converter = Converters.StringListConverter.class)
    private List<String> stop;
    // @Comment("Positive values penalize new tokens based on whether they appear in the text so far")
    @Range(min = -2, max = 2, message = "Number between -2.0 and 2.0.")
    private Double presencePenalty;     // default 0
    // @Comment("Positive values penalize new tokens based on their existing frequency in the text so far")
    @Range(min = -2, max = 2, message = "Number between -2.0 and 2.0.")
    private Double frequencyPenalty;    // default 0
    // @Comment("A unique identifier representing your end-user")
    private String endUser;             // default null
    @Lob
    // @Comment("Modify the likelihood of specified tokens appearing in the completion.")
    @Convert(converter = Converters.IntegerMapConverter.class)
    private Map<String, Integer> logitBias = new HashMap<>();

    Conversation(ConversationId conversationId, UserId ownerUserId, String name,
                 ChatModel model, Boolean sendHistory, Double temperature, Double topP, Integer maxTokens,
                 String systemMessage, Integer choices, List<String> stop, Double presencePenalty,
                 Double frequencyPenalty, String endUser, Map<String, Integer> logitBias,
                 UserEquityChecker equityChecker) {
        if (!equityChecker.allowUseModel(model)) {
            throw new IllegalArgumentException("No permission to use model " + model);
        }

        this.conversationId = conversationId;
        this.ownerUserId = ownerUserId;
        this.name = Objects.requireNonNullElse(name, "Unnamed conversation");

        this.model = Objects.requireNonNullElse(model, ChatModel.GPT3_5);
        this.sendHistory = Objects.requireNonNullElse(sendHistory, true);
        this.temperature = Objects.requireNonNullElse(temperature, 0.7);
        this.topP = Objects.requireNonNullElse(topP, 1.0);
        this.maxTokens = Objects.requireNonNullElse(maxTokens, 2000);

        this.systemMessage = systemMessage;
        this.choices = Objects.requireNonNullElse(choices, 1);
        this.stop = stop;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.endUser = endUser;
        this.logitBias = logitBias;
    }

    public void update(String name, ChatModel model, Boolean sendHistory,
                       Double temperature, Double topP, Integer maxTokens,
                       UserEquityChecker equityChecker) {
        if (!equityChecker.allowUseModel(model)) {
            throw new IllegalArgumentException("No permission to use model " + model);
        }

        this.name = name;
        this.model = model;
        this.sendHistory = sendHistory;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
    }

    public void enabledSendHistory(Boolean enabled) {
        this.sendHistory = BooleanUtils.isTrue(enabled);
    }

    public ConversationMessage newMessage(Message message) {
        return new ConversationMessage(
                new ConversationMessageId(IdentityGenerator.nextIdentity()),
                this.getConversationId(),
                message,
                MessageStatus.Processing
        );
    }

    public ConversationMessage newMessage(Message message, MessageStatus status) {
        return new ConversationMessage(
                new ConversationMessageId(IdentityGenerator.nextIdentity()),
                this.getConversationId(),
                message,
                status
        );
    }

    public MessageConfig messageConfig() {
        return new MessageConfig(
                this.model,
                this.temperature,
                this.topP,
                this.maxTokens,
                this.systemMessage,
                this.choices,
                this.stop,
                this.presencePenalty,
                this.frequencyPenalty,
                this.endUser,
                this.logitBias
        );
    }

    public Flux<Message> sendMessage(ConversationMessageService messageService,
                                     Requester requester, ConversationMessage message) {
        return messageService.sendConversionMessage(this, requester, message)
                .doOnError(e -> message.failed(e.getMessage()));
    }

    public ConversationArchive archive() {
        return new ConversationArchive(
                new ConversationArchiveId(IdentityGenerator.nextIdentity()),
                this.getConversationId(),
                this.getOwnerUserId(),
                this.getName(),
                this.getModel(),
                this.getSendHistory(),
                this.getTemperature(),
                this.getTopP(),
                this.getMaxTokens(),
                this.getSystemMessage(),
                this.getChoices(),
                this.getStop(),
                this.getPresencePenalty(),
                this.getFrequencyPenalty(),
                this.getEndUser(),
                this.getLogitBias()
        );
    }
}
