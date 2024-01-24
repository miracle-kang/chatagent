package com.miraclekang.chatgpt.common.facade;


import com.miraclekang.chatgpt.common.facade.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${remote.identity.url:http://identity}")
public interface IdentityServiceFacade {

    @GetMapping("/api/internal/users/{userId}/info")
    UserInfoDTO userInfo(@PathVariable String userId);

    @GetMapping("/api/internal/users/{userId}/username")
    String username(@PathVariable String userId);

    @GetMapping("/api/internal/users/{userId}/exists")
    Boolean existsUser(@PathVariable String userId);
}
