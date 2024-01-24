package com.miraclekang.chatgpt.identity.port.adapter.restapi.open;

import com.miraclekang.chatgpt.common.access.AuthenticateToken;
import com.miraclekang.chatgpt.identity.application.user.AuthenticationService;
import com.miraclekang.chatgpt.identity.application.user.command.RegisterCommand;
import com.miraclekang.chatgpt.identity.port.adapter.restapi.open.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@PreAuthorize("permitAll()")
@RequestMapping("/api/open/auth")
@Tag(name = "Authentication APIs", description = "Authentication APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/phone/check")
    @Operation(summary = "Check phone", description = "Check phone number")
    public Mono<Boolean> checkPhone(@Valid @RequestBody PhoneRequest request) {
        return authenticationService.checkPhone(request.getPhoneArea(), request.getPhoneNumber());
    }

    @PostMapping("/phone/code")
    @Operation(summary = "Phone auth code", description = "Send phone auth code")
    public Mono<Void> sendPhoneAuthCode(@Valid @RequestBody PhoneVerificationRequest request) {
        return authenticationService.sendPhoneAuthCode(
                request.getPhoneArea(),
                request.getPhoneNumber(),
                request.getType());
    }

    @PostMapping("/phone")
    @Operation(summary = "Auth by phone", description = "Authenticate by phone number")
    public Mono<AuthenticateToken> authPhone(@Valid @RequestBody PhoneAuthRequest request) {
        return authenticationService.authenticateByPhone(
                request.getPhoneArea(),
                request.getPhoneNumber(),
                request.getAuthCode());
    }

    @PostMapping("/email/check")
    @Operation(summary = "Check email", description = "Check email address")
    public Mono<Boolean> checkEmail(@Valid @RequestBody EmailRequest request) {
        return authenticationService.checkEmail(request.getEmailAddress());
    }

    @PostMapping("/email/code")
    @Operation(summary = "Email auth code", description = "Send email auth code")
    public Mono<Void> sendEmailAuthCode(@Valid @RequestBody EmailVerificationRequest request) {
        return authenticationService.sendEmailAuthCode(request.getEmailAddress(), request.getType());
    }

    @PostMapping("/email")
    @Operation(summary = "Auth by email", description = "Authenticate by email address")
    public Mono<AuthenticateToken> authEmail(@Valid @RequestBody EmailAuthRequest request) {
        return authenticationService.authenticateByEmail(
                request.getEmailAddress(),
                request.getAuthCode());
    }

    @PostMapping("/password")
    @Operation(summary = "Auth by password", description = "Authenticate by password")
    public Mono<AuthenticateToken> authPassword(@Valid @RequestBody PasswordAuthRequest request) {
        return authenticationService.authenticateByPassword(
                request.getIdentity(),
                request.getPassword());
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register user")
    public Mono<AuthenticateToken> register(@Valid @RequestBody RegisterCommand command) {
        return authenticationService.register(command);
    }

    @PostMapping("/token/renewal")
    @Operation(summary = "Renewal token", description = "Renewal token")
    public Mono<AuthenticateToken> renewalToken() {
        return authenticationService.renewalToken();
    }
}
