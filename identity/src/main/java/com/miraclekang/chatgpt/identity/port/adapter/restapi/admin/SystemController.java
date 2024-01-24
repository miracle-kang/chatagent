package com.miraclekang.chatgpt.identity.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.identity.application.common.SystemProfileService;
import com.miraclekang.chatgpt.identity.application.common.command.UpdateSystemProfileCommand;
import com.miraclekang.chatgpt.identity.application.common.querystack.SystemProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/system")
@Tag(name = "System APIs", description = "System APIs")
public class SystemController {

    private final SystemProfileService systemProfileService;

    public SystemController(SystemProfileService systemProfileService) {
        this.systemProfileService = systemProfileService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get system profile", description = "Get system profile")
    public Mono<SystemProfileDTO> getSystemProfile() {
        return systemProfileService.get();
    }

    @PutMapping("/profile")
    @Operation(summary = "Update system profile", description = "Update system profile")
    public Mono<SystemProfileDTO> updateSystemProfile(@RequestBody UpdateSystemProfileCommand command) {
        return systemProfileService.update(command);
    }
}
