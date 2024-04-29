package com.miraclekang.chatgpt.common.facade;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "agent-service", url = "${remote.agent.url:http://agent}")
public interface AssistantServiceFacade {
}
