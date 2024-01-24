package com.miraclekang.chatgpt.identity.port.adapter.restapi.open;

import com.miraclekang.chatgpt.identity.application.user.UserInvitationService;
import com.miraclekang.chatgpt.identity.application.user.UserService;
import com.miraclekang.chatgpt.identity.application.user.command.UpdateUserProfileCommand;
import com.miraclekang.chatgpt.identity.application.user.querystack.*;
import com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto.ChangePasswordRequest;
import com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto.NewInvitationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/open/user")
@Tag(name = "User APIs", description = "User APIs")
public class UserController {

    private final UserService userService;
    private final UserInvitationService invitationService;

    public UserController(UserService userService, UserInvitationService invitationService) {
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @GetMapping("/info")
    @Operation(summary = "Current user info", description = "Current user info")
    public Mono<PublicUserInfoDTO> currentUserInfo() {
        return userService.currentUserInfo();
    }

    @GetMapping("/profile")
    @Operation(summary = "Current user profile", description = "Current user profile")
    public Mono<UserProfileDTO> getProfile() {
        return userService.currentUserProfile();
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update user profile")
    public Mono<UserProfileDTO> updateProfile(@RequestBody UpdateUserProfileCommand command) {
        return userService.updateProfile(command);
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Change password")
    public Mono<Void> changePassword(@RequestBody ChangePasswordRequest command) {
        return userService.changePassword(command.getPassword(), command.getNewPassword());
    }

    @PostMapping("/invitations")
    @Operation(summary = "New invitation", description = "New invitation")
    public Mono<InvitationDTO> newInvitation(@RequestBody NewInvitationRequest request) {
        return invitationService.newInvitation(request.getType());
    }

    @PageableAsQueryParam
    @GetMapping("/invitations")
    @Operation(summary = "Query invitations", description = "Query invitations")
    public Mono<Page<InvitationDTO>> queryInvitations(@ParameterObject Pageable pageable) {
        return invitationService.queryUserInvitations(pageable);
    }

    @GetMapping("/invitations/{invitationId}")
    @Operation(summary = "Invitation detail", description = "Invitation detail")
    public Mono<InvitationDetailDTO> getInvitationDetail(@PathVariable String invitationId) {
        return invitationService.getInvitationDetail(invitationId);
    }

    @DeleteMapping("/invitations/{invitationId}")
    @Operation(summary = "Invalidate invitation", description = "Invalidate invitation")
    public Mono<InvitationDTO> invalidateInvitation(@PathVariable String invitationId) {
        return invitationService.invalidateInvitation(invitationId);
    }

    @PageableAsQueryParam
    @GetMapping("/invitations/{invitationId}/acceptors")
    @Operation(summary = "Invitation acceptors", description = "Invitation acceptors")
    public Mono<Page<InvitationAccepterDTO>> queryInvitationAcceptors(@PathVariable String invitationId,
                                                                      @ParameterObject Pageable pageable) {
        return invitationService.queryInvitationAcceptors(invitationId, pageable);
    }
}
