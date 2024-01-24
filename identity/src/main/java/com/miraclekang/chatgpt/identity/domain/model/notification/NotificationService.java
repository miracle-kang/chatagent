package com.miraclekang.chatgpt.identity.domain.model.notification;

import com.miraclekang.chatgpt.identity.domain.model.identity.Email;

public interface NotificationService {

    void notifyDingTalk(Notification notification);

    void notifyEmail(Email email, Notification notification);

    void notifyHtmlEmail(Email email, Notification notification);
}
