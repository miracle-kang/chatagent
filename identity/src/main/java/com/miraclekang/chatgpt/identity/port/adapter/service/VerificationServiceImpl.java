package com.miraclekang.chatgpt.identity.port.adapter.service;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.identity.Phone;
import com.miraclekang.chatgpt.identity.domain.model.identity.VerificationService;
import com.miraclekang.chatgpt.identity.domain.model.identity.VerificationType;
import com.miraclekang.chatgpt.identity.domain.model.notification.Notification;
import com.miraclekang.chatgpt.identity.domain.model.notification.NotificationService;
import com.miraclekang.chatgpt.identity.port.adapter.service.authcode.AuthCodeValidator;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfig;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class VerificationServiceImpl implements VerificationService {

    @Value("${auth.allow-mock:false}")
    private Boolean allowMock = false;
    @Value("${auth.mock-auth-code:666666}")
    private String mockAuthCode = "666666";

    private final NotificationService notificationService;
    private final FreeMarkerConfig freeMarkerConfig;

    private final AuthCodeValidator authCodeValidator;

    public VerificationServiceImpl(NotificationService notificationService,
                                   FreeMarkerConfig freeMarkerConfig,
                                   AuthCodeValidator authCodeValidator) {
        this.notificationService = notificationService;
        this.freeMarkerConfig = freeMarkerConfig;
        this.authCodeValidator = authCodeValidator;
    }

    @Override
    public String sendVerificationCode(Phone phone, VerificationType type) {
        throw new IllegalArgumentException("Phone verification is not supported yet.");
    }

    @Override
    public String sendVerificationCode(Email email, VerificationType type) {
        String authCode = authCodeValidator.generateAuthCode(email.getAddress(), 30);
        String content;
        try {
            String templateName = String.format("verification-%s.ftl", type.name().toLowerCase());
            Template template = freeMarkerConfig.getConfiguration().getTemplate(templateName);
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, Map.of("authCode", authCode));
        } catch (IOException | TemplateException e) {
            log.error("Failed to render verification email template", e);
            throw new RuntimeException(e);
        }

        notificationService.notifyHtmlEmail(email, new Notification(
                "欢迎使用春蚕AI",
                content
        ));
        return authCode;
    }

    @Override
    public Boolean checkVerificationCode(Phone phone, String code) {
        Validate.notNull(phone, "Phone number must be provided.");
        Validate.notBlank(code, "Verification code must not be null.");

        // Mock Auth Code
        if (BooleanUtils.isTrue(allowMock) && StringUtils.equals(mockAuthCode, code)) {
            return true;
        }
        return authCodeValidator.validateAuthCode(phone.getNumber(), code);
    }

    @Override
    public Boolean checkVerificationCode(Email email, String code) {
        Validate.notNull(email, "Email address must be provided.");
        Validate.notBlank(code, "Verification code must not be null.");

        // Mock Auth Code
        if (BooleanUtils.isTrue(allowMock) && StringUtils.equals(mockAuthCode, code)) {
            return true;
        }
        return authCodeValidator.validateAuthCode(email.getAddress(), code);
    }
}
