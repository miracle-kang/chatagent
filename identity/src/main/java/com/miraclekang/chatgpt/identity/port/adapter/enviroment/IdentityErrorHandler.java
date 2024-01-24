package com.miraclekang.chatgpt.identity.port.adapter.enviroment;

import com.miraclekang.chatgpt.common.global.ErrorMessage;
import com.miraclekang.chatgpt.common.global.GlobalErrorHandler;
import com.miraclekang.chatgpt.common.exception.ApplicationException;
import com.miraclekang.chatgpt.common.exception.DomainException;
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
public class IdentityErrorHandler extends GlobalErrorHandler {

    public IdentityErrorHandler(DefaultErrorAttributes defaultErrorAttributes,
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
        } else if (e instanceof DomainException de) {
            return new ErrorMessage(de.getExceptionCode(),
                    new CodeAndMessage(de.getExceptionCode().code(), de.getMessage()), de.getArgs());
        }

        return super.resolveErrorMessage(e);
    }

}
