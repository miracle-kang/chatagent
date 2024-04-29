package com.miraclekang.chatgpt.agent.port.adapter.thirdparty.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miraclekang.chatgpt.agent.port.adapter.thirdparty.common.HttpClientFactory;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class OpenAIClient {

    private static final String OPENAI_BASE_URL = "https://api.openai.com";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final String token;
    private final String defaultSystemMessage;

    public OpenAIClient(String token) {
        this(token, """
                You are a friendly and helpful Securities industry assistant.
                """);
    }

    public OpenAIClient(String token, String defaultSystemMessage) {
        this(token, defaultSystemMessage, "none");
    }

    public OpenAIClient(String token, String defaultSystemMessage, String proxy) {
        this.httpClient = HttpClientFactory.newHttpClient(proxy, Duration.ofMinutes(5));

        this.token = token;
        this.defaultSystemMessage = defaultSystemMessage;

        this.objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    public List<ChatMessage> newChat(String question) {
        return newChat(null, question);
    }

    public List<ChatMessage> newChat(String question, Double temperature) {
        return newChat(null, question, temperature);
    }

    public List<ChatMessage> newChat(String systemMessage, String question) {
        return newChat(systemMessage, question, null);
    }

    public List<ChatMessage> newChat(String systemMessage, String question, Double temperature) {
        return continueChat(
                List.of(new ChatMessage(ChatMessageRole.SYSTEM.value(),
                        systemMessage == null ? defaultSystemMessage : systemMessage)),
                question, temperature);
    }

    public List<ChatMessage> continueChat(List<ChatMessage> previousMessages, String question) {
        return continueChat(previousMessages, question, null);
    }

    public List<ChatMessage> continueChat(List<ChatMessage> previousMessages, String question, Double temperature) {
        List<ChatMessage> messages = Stream.concat(previousMessages.stream(),
                        Stream.of(new ChatMessage(ChatMessageRole.USER.value(), question)))
                .toList();
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(temperature)
                .build();
        return chatCompletion(request)
                .toStream().toList();
    }

    public Flux<ChatMessage> chatCompletion(ChatCompletionRequest request) {
        var httpRequest = buildHttpPost("/v1/chat/completions", request);
        return Flux.push(emitter -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenApply(this::handleChatResponseStream)
                .thenAccept(stream -> stream.forEach(emitter::next))
                .whenCompleteAsync(($, err) -> {
                    if (err != null) {
                        emitter.error(err);
                        return;
                    }
                    emitter.complete();
                }));
    }

    private Stream<ChatMessage> handleChatResponseStream(HttpResponse<Stream<String>> response) {
        if (response.statusCode() != 200) {
            String error = response.body().reduce((s1, s2) -> s1 + s2).orElse("{}");
            throw new OpenAiHttpException(readValue(error, OpenAiError.class),
                    null, response.statusCode());
        }
        return response.body()
                .filter(line -> line.startsWith("data: "))
                .map(line -> {
                    try {
                        log.trace(">>>>>> Open AI response {}", line);
                        JsonNode jsonNode = objectMapper.readTree(line.substring(6));
                        if (!jsonNode.has("choices")) {
                            return null;
                        }
                        ArrayNode choices = (ArrayNode) jsonNode.get("choices");
                        if (choices.isEmpty()) {
                            return null;
                        }
                        JsonNode choice = choices.get(0);
                        if (choice.has("delta")) {
                            return objectMapper.convertValue(choice.get("delta"), ChatMessage.class);
                        } else if (choice.has("message")) {
                            return objectMapper.convertValue(choice.get("message"), ChatMessage.class);
                        }
                        return null;
                    } catch (JsonProcessingException e) {
                        // ignore.
                        return null;
                    }
                }).filter(Objects::nonNull);
    }

    private HttpRequest buildHttpPost(String path, Object body) {
        String strBody = writeValue(body);
        log.debug(">>>> Open AI request data: {}", strBody);
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(strBody);
        return HttpRequest.newBuilder(URI.create(OPENAI_BASE_URL + path))
                .POST(bodyPublisher)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .build();
    }

    private <T> T readValue(String str, Class<T> type) {
        try {
            return objectMapper.readValue(str, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeValue(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
