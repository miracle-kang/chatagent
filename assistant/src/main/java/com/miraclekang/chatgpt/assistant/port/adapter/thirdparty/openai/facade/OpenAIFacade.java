package com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.openai.facade;

import com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.common.Authorize;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import reactor.core.publisher.Flux;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Authorize(method = Authorize.Method.Bearer)
public interface OpenAIFacade {

    @POST @Path("/v1/chat/completions")
    Flux<ChatCompletionResult> createChatCompletion(ChatCompletionRequest request);
}
