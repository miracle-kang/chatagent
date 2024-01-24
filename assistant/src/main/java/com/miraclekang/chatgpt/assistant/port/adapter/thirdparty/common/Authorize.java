package com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Authorize {

    /**
     * Authorization method
     */
    Method method() default Method.Basic;

    enum Method {
        Basic,
        Bearer,
    }
}
