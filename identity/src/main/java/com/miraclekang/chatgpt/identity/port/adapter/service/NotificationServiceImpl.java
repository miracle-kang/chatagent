package com.miraclekang.chatgpt.identity.port.adapter.service;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;
import com.miraclekang.chatgpt.identity.domain.model.notification.Notification;
import com.miraclekang.chatgpt.identity.domain.model.notification.NotificationService;
import com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk.DingTalkClient;
import com.miraclekang.chatgpt.identity.port.adapter.thirdparty.dingtalk.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationServiceImpl implements NotificationService {

    private static final String EMAIL_SENDER_NAME = "春蚕AI";

    private final DingTalkClient dingTalkClient;
    private final JavaMailSender javaMailSender;

    private final String emailSender;

    public NotificationServiceImpl(@Value("${third-party.dingtalk.token}") String dingtalkToken,
                                   @Value("${third-party.dingtalk.secret}") String dingtalkSecret,
                                   @Value("${spring.mail.sender}") String emailSender,
                                   JavaMailSender javaMailSender) {
        this.dingTalkClient = new DingTalkClient(dingtalkToken, dingtalkSecret);
        this.emailSender = emailSender;
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Override
    public void notifyDingTalk(Notification notification) {
        String msg = StringUtils.isBlank(notification.getTitle()) ? notification.getContent()
                : notification.getTitle() + "\n" + notification.getContent();
        try {
            dingTalkClient.sendTextMessage(new TextMessage(msg));
            log.debug(">>>>>> Sent dingtalk text message: {}", msg);
        } catch (Exception ex) {
            log.error("发送钉钉消息异常", ex);
        }
    }

    @Async
    @Override
    public void notifyEmail(Email email, Notification notification) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), "utf-8");
            helper.setFrom(emailSender, EMAIL_SENDER_NAME);
            helper.setTo(email.getAddress());
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getContent(), false);

            javaMailSender.send(helper.getMimeMessage());
            log.debug(">>>>>> Sent email to {} with message: {}", email.getAddress(), notification);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email.getAddress(), e);
        }
    }

    @Async
    @Override
    public void notifyHtmlEmail(Email email, Notification notification) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), "utf-8");
            helper.setFrom(emailSender, EMAIL_SENDER_NAME);
            helper.setTo(email.getAddress());
            helper.setSubject(notification.getTitle());
            helper.setText(notification.getContent(), true);

            javaMailSender.send(helper.getMimeMessage());
            log.debug(">>>>>> Sent email to {} with message: {}", email.getAddress(), notification);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email.getAddress(), e);
        }
    }
}
