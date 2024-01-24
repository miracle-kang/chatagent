package com.miraclekang.chatgpt.common.facade;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "assistant-service", url = "${remote.assistant.url:http://assistant}")
public interface AssistantServiceFacade {
}
