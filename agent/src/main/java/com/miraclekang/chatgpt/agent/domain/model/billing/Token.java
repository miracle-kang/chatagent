package com.miraclekang.chatgpt.agent.domain.model.billing;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.miraclekang.chatgpt.common.model.ValueObject;
import com.miraclekang.chatgpt.agent.domain.model.chat.Message;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.Stream;

public class Token extends ValueObject {

    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();
    public static Token ZERO = new Token(List.of());

    private final List<Integer> tokens;
    private final int extTokens;

    private Token(List<Integer> tokens) {
        this(tokens, 0);
    }

    private Token(List<Integer> tokens, int extTokens) {
        this.tokens = tokens;
        this.extTokens = extTokens;
    }

    public Token add(Token other) {
        Validate.notNull(other, "Token to be added must provided.");

        return new Token(Stream.concat(tokens.stream(), other.tokens.stream()).toList(),
                extTokens + other.extTokens);
    }

    public int numTokens() {
        return tokens.size() + extTokens;
    }

    public List<Integer> getTokens() {
        return tokens;
    }

    public static Token encodeMessage(String model, Message message) {

        Encoding encoder = REGISTRY.getEncodingForModel(modelType(model));

        return new Token(
                encoder.encode(message.getContent()),
                5   // role is always required and always 1 token
                // every message follows <im_start>{role/name}\n{content}<im_end>, and always 4 tokens
        );
    }

    public static Token encode(String model, String content) {
        Encoding encoder = REGISTRY.getEncodingForModel(modelType(model));

        return new Token(encoder.encode(content), 0);
    }

    private static ModelType modelType(String model) {
        for (ModelType value : ModelType.values()) {
            if (value.getName().equalsIgnoreCase(model)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported model " + model);
    }
}
