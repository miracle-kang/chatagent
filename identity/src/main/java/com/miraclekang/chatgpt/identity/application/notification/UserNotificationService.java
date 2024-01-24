package com.miraclekang.chatgpt.identity.application.notification;

import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserPasswordReset;
import com.miraclekang.chatgpt.identity.domain.model.identity.user.UserRegistered;
import com.miraclekang.chatgpt.identity.domain.model.notification.Notification;
import com.miraclekang.chatgpt.identity.domain.model.notification.NotificationService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfig;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class UserNotificationService {

    private final NotificationService notificationService;
    private final FreeMarkerConfig freeMarkerConfig;

    public UserNotificationService(NotificationService notificationService,
                                   FreeMarkerConfig freeMarkerConfig) {
        this.notificationService = notificationService;
        this.freeMarkerConfig = freeMarkerConfig;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyUserRegistered(UserRegistered event) {

        System.out.println(">>>>>> UserNotificationService.notifyUserRegistered");
        if (event.getRandPassword() == null) {
            log.info(">>>>>> User {} registered", event.getUsername().getUsername());
        } else {
            log.info(">>>>>> User {} registered with rand password: {}",
                    event.getUsername().getUsername(), event.getRandPassword());
        }
        if (event.getEmail() == null) {
            log.warn(">>>>>> User {} has no email, skip sending welcome email", event.getUsername().getUsername());
            return;
        }

        Map<String, String> userProperties = Map.of(
                "username", event.getUsername().getUsername(),
                "password", event.getRandPassword() == null ? "" : event.getRandPassword()
        );

        String content;
        try {
            Template template = freeMarkerConfig.getConfiguration().getTemplate("welcome.ftl");
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, Map.of("user", userProperties));
        } catch (IOException | TemplateException e) {
            log.error("Failed to render welcome email template", e);
            throw new RuntimeException(e);
        }

        notificationService.notifyEmail(event.getEmail(), new Notification(
                "[春蚕AI] 欢迎使用春蚕AI",
                content
        ));
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyUserPasswordReset(UserPasswordReset event) {
        log.info(">>>>>> User {} password reset to {}", event.getUsername().getUsername(), event.getRandPassword());
        if (event.getEmail() == null) {
            log.warn(">>>>>> User {} has no email, skip sending reset password email", event.getUsername().getUsername());
            return;
        }

        Map<String, String> userProperties = Map.of(
                "username", event.getUsername().getUsername(),
                "password", event.getRandPassword() == null ? "" : event.getRandPassword()
        );

        String content;
        try {
            Template template = freeMarkerConfig.getConfiguration().getTemplate("password-reset.ftl");
            content = FreeMarkerTemplateUtils.processTemplateIntoString(template, Map.of("user", userProperties));
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

        notificationService.notifyEmail(event.getEmail(), new Notification(
                "[春蚕AI] 密码重置通知",
                content
        ));
    }
}
