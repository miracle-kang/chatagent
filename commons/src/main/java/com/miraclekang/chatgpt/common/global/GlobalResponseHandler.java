package com.miraclekang.chatgpt.common.global;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miraclekang.chatgpt.common.restapi.NoBodyWarp;
import lombok.Data;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.AbstractMessageWriterResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 响应数据全局处理
 */
public class GlobalResponseHandler extends AbstractMessageWriterResultHandler implements HandlerResultHandler {

    private static final Object NULL_OBJECT = new Object();


    private final List<String> responseNoWrapPaths = List.of(
            "doc.html",
            "/actuator/**",
            "/webjars/**",
            "/swagger-ui*/**",
            "/api-docs*/**",
            "/v3/api-docs*/**"
    );
    private final List<Class<?>> responseNoWrapClasses = List.of(
            void.class, Void.class
    );

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final ObjectMapper objectMapper;


    public GlobalResponseHandler(ServerCodecConfigurer serverCodecConfigurer,
                                 RequestedContentTypeResolver contentTypeResolver,
                                 ReactiveAdapterRegistry adapterRegistry,
                                 ObjectMapper objectMapper) {
        super(serverCodecConfigurer.getWriters(), contentTypeResolver, adapterRegistry);
        this.objectMapper = objectMapper;
        setOrder(99);
    }

    @Override
    public boolean supports(HandlerResult result) {
        MethodParameter returnType = result.getReturnTypeSource();
        Class<?> containingClass = returnType.getContainingClass();
        if (AnnotatedElementUtils.hasAnnotation(containingClass, NoBodyWarp.class)
                || returnType.hasMethodAnnotation(NoBodyWarp.class)) {
            return false;
        }
        if (returnType.getMethod() == null) {
            return false;
        }
        if ((returnType.getGenericParameterType() instanceof Class<?> clazz)
                && (clazz.isPrimitive() || responseNoWrapClasses.stream().anyMatch(c -> c.isAssignableFrom(clazz)))) {
            return false;
        }

        String name = returnType.getMethod().getName();
        return !name.equals("uiConfiguration") && !name.equals("swaggerResources") && !name.equals("getDocumentation");
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        String requestPath = exchange.getRequest().getPath().value();
        if (responseNoWrapPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath))) {
            return writeBody(result.getReturnValue(), result.getReturnTypeSource(), exchange);
        }

        Object body = result.getReturnValue();
        MethodParameter bodyTypeParameter = result.getReturnTypeSource();

        Object writeBody;
        if (result.getReturnValue() == null) {
            writeBody = ResponseBase.ok(NULL_OBJECT);
        } else if (body instanceof JsonNode) {
            writeBody = ResponseBase.ok(body);
        } else if (body instanceof Mono<?> mono) {
            writeBody = mono.map(content -> {
                if (content == null) {
                    return ResponseBase.ok(NULL_OBJECT);
                }
                if (content instanceof ResponseBase<?>) {
                    return content;
                }
                if (content instanceof CharSequence) {
                    try {
                        return objectMapper.writeValueAsString(ResponseBase.ok(content));
                    } catch (JsonProcessingException e) {
                        logger.error("Error serialize char-sequence to json", e);
                        return content;
                    }
                }
//                return ResponseBase.ok(content);
                return content;
            });
        } else if (body instanceof Flux<?> flux) {
            writeBody = flux.collectList().map(data -> ResponseBase.ok(Objects.requireNonNullElse(data, NULL_OBJECT)));
        } else {
            writeBody = ResponseBase.ok(body);
        }

        return writeBody(writeBody, bodyTypeParameter, exchange);
    }


    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new CustomDateEditor(objectMapper.getDateFormat(), true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(objectMapper.getDateFormat(), true));
    }

    @Data
    static class ResponseBase<T> {

        private String code;
        private String msg;
        private T data;

        public ResponseBase(String code, String msg, T data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public ResponseBase() {
        }

        public static <T> ResponseBase<T> of(String code, String msg, T data) {
            return new ResponseBase<T>(code, msg, data);
        }

        public static <T> ResponseBase<T> ok(T data) {
            return new ResponseBase<T>(ExceptionCode.OK.code(), ExceptionCode.OK.code(), data);
        }
    }
}