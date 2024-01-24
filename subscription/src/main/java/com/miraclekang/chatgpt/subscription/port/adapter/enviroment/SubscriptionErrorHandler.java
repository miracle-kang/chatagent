package com.miraclekang.chatgpt.subscription.port.adapter.enviroment;

import com.miraclekang.chatgpt.common.global.ErrorMessage;
import com.miraclekang.chatgpt.common.global.GlobalErrorHandler;
import com.miraclekang.chatgpt.common.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Order(-1)
public class SubscriptionErrorHandler extends GlobalErrorHandler {

    public SubscriptionErrorHandler(DefaultErrorAttributes defaultErrorAttributes,
                                    WebProperties webProperties,
                                    ApplicationContext applicationContext,
                                    ServerCodecConfigurer serverCodecConfigurer,
                                    MessageSource messageSource) {
        super(defaultErrorAttributes, webProperties, applicationContext, serverCodecConfigurer, messageSource);
    }

    protected ErrorMessage resolveErrorMessage(Exception e) {
        if (e instanceof ApplicationException ae) {
            // 平台异常
            return new ErrorMessage(ae.getExceptionCode(),
                    new CodeAndMessage(ae.getCode(), ae.getMessage()), ae.getArgs());
        }

        return super.resolveErrorMessage(e);
    }

}
