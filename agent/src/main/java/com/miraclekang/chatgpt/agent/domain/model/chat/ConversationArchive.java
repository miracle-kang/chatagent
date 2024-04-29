package com.miraclekang.chatgpt.agent.domain.model.chat;

import com.miraclekang.chatgpt.common.repo.Converters;
import com.miraclekang.chatgpt.common.model.BaseEntity;
import com.miraclekang.chatgpt.agent.domain.model.identity.UserId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "UK_conversation_archive_id", columnList = "archiveId", unique = true),
        @Index(name = "IDX_conversation_archive_owner_user_id", columnList = "ownerUserId")
})
public class ConversationArchive extends BaseEntity {

    // @Comment("Conversation Archive ID")
    @AttributeOverride(name = "id", column = @Column(name = "archiveId", length = 64))
    private ConversationArchiveId archiveId;

    // @Comment("Conversation ID")
    @AttributeOverride(name = "id", column = @Column(name = "conversationId", length = 64))
    private ConversationId conversationId;
    // @Comment("Owner User")
    @AttributeOverride(name = "id", column = @Column(name = "ownerUserId", length = 64))
    private UserId ownerUserId;
    // @Comment("Conversation name")
    private String name;


    // Configuration for this conversation
    // @Comment("ID of the model to use.")
    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private ChatModel model;        // default GPT3_5
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

    public ConversationArchive(ConversationArchiveId archiveId, ConversationId conversationId, UserId ownerUserId,
                               String name, ChatModel model, Boolean sendHistory, Double temperature, Double topP,
                               Integer maxTokens, String systemMessage, Integer choices, List<String> stop,
                               Double presencePenalty, Double frequencyPenalty, String endUser,
                               Map<String, Integer> logitBias) {
        this.archiveId = archiveId;
        this.conversationId = conversationId;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.model = model;
        this.sendHistory = sendHistory;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.systemMessage = systemMessage;
        this.choices = choices;
        this.stop = stop;
        this.presencePenalty = presencePenalty;
        this.frequencyPenalty = frequencyPenalty;
        this.endUser = endUser;
        this.logitBias = logitBias;
    }
}
