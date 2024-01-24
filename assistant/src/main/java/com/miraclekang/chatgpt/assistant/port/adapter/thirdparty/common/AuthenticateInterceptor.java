package com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.common;

import feign.MethodMetadata;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class AuthenticateInterceptor implements RequestInterceptor {

    private final String customHeader;
    private final String authToken;

    public AuthenticateInterceptor(String authToken) {
        this(null, authToken);
    }

    public AuthenticateInterceptor(String customHeader, String authToken) {
        this.customHeader = customHeader;
        this.authToken = authToken;
    }

    @Override
    public void apply(RequestTemplate template) {
        MethodMetadata methodMetadata = template.methodMetadata();
        Authorize annotation = methodMetadata.targetType().getAnnotation(Authorize.class);
        if (annotation == null) {
            annotation = methodMetadata.method().getAnnotation(Authorize.class);
        }
        if (annotation == null) {
            return;
        }

        if (customHeader != null) {
            template.header(customHeader, authToken);
            return;
        }

        String token;
        if (annotation.method() == Authorize.Method.Bearer) {
            token = "Bearer " + authToken;
        } else {
            token = authToken;
        }
        template.header("Authorization", token);
    }
}
