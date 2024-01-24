package com.miraclekang.chatgpt.identity.port.adapter.restapi.admin;

import com.miraclekang.chatgpt.common.restapi.SearchCriteriaParam;
import com.miraclekang.chatgpt.identity.application.administrator.AdministratorService;
import com.miraclekang.chatgpt.identity.application.administrator.command.NewAdministratorCommand;
import com.miraclekang.chatgpt.identity.application.administrator.command.UpdateAdministratorCommand;
import com.miraclekang.chatgpt.identity.application.administrator.querystack.AdministratorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin/administrators")
@Tag(name = "Administrator APIs", description = "Administrator APIs")
public class AdministratorController {

    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @PostMapping
    @Operation(summary = "Add administrator", description = "Add a new administrator")
    public Mono<AdministratorDTO> addAdministrator(@Valid @RequestBody NewAdministratorCommand command) {
        return administratorService.addAdministrator(command);
    }

    @PutMapping("/{administratorId}")
    @Operation(summary = "Update administrator", description = "Update an administrator")
    public Mono<AdministratorDTO> updateAdministrator(@PathVariable String administratorId,
                                                      @Valid @RequestBody UpdateAdministratorCommand command) {
        return administratorService.updateAdministrator(administratorId, command);
    }

    @GetMapping("/{administratorId}")
    @Operation(summary = "Get administrator", description = "Get an administrator")
    public Mono<AdministratorDTO> getAdministrator(@PathVariable String administratorId) {
        return administratorService.getAdministrator(administratorId);
    }

    @DeleteMapping("/{administratorId}")
    @Operation(summary = "Delete administrator", description = "Delete an administrator")
    public Mono<Void> deleteAdministrator(@PathVariable String administratorId) {
        return administratorService.deleteAdministrator(administratorId);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Query administrators", description = "Query administrators")
    public Mono<Page<AdministratorDTO>> queryAdministrators(@ParameterObject SearchCriteriaParam criteriaParam,
                                                            @ParameterObject Pageable pageable) {
        return administratorService.queryAdministrators(criteriaParam.toCriteria(), pageable);
    }

    @PutMapping("/{administratorId}/enable")
    @Operation(summary = "Enable administrator", description = "Enable an administrator")
    public Mono<Void> enableAdministrator(@PathVariable String administratorId) {
        return administratorService.enableAdministrator(administratorId);
    }

    @PutMapping("/{administratorId}/disable")
    @Operation(summary = "Disable administrator", description = "Disable an administrator")
    public Mono<Void> disableAdministrator(@PathVariable String administratorId) {
        return administratorService.disableAdministrator(administratorId);
    }

    @PutMapping("/{administratorId}/reset-password")
    @Operation(summary = "Reset administrator password", description = "Reset an administrator password")
    public Mono<Void> resetAdministratorPassword(@PathVariable String administratorId) {
        return administratorService.resetAdministratorPassword(administratorId);
    }
}
