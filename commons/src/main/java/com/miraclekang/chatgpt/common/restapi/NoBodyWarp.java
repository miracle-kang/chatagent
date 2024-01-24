package com.miraclekang.chatgpt.common.restapi;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoBodyWarp {
    boolean nowarp() default true;
}
