package com.miraclekang.chatgpt.common.global;

import com.miraclekang.chatgpt.common.exception.DomainException;
import com.miraclekang.chatgpt.common.reactive.Requester;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.miraclekang.chatgpt.common.reactive.Requester.SUPPORTED_LOCALES;


@Slf4j
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    private final MessageSource messageSource;

    public GlobalErrorHandler(DefaultErrorAttributes defaultErrorAttributes,
                              WebProperties webProperties,
                              ApplicationContext applicationContext,
                              ServerCodecConfigurer serverCodecConfigurer,
                              MessageSource messageSource) {
        super(defaultErrorAttributes, webProperties.getResources(), applicationContext);
        this.messageSource = messageSource;
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    public Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Throwable throwable = getError(request);
        if (!(throwable instanceof Exception exception)) {
            return Mono.empty();
        }

        ErrorMessage message = resolveErrorMessage(exception);
        ExceptionCode exceptionCode = message.getExceptionCode();
        Object data = message.getContent();
        Object[] args = message.getArgs();

        Locale locale = Locale.lookup(request.headers().asHttpHeaders().getAcceptLanguage(), SUPPORTED_LOCALES);
        if (locale == null) {
            // By default, use simplified chinese
            locale = Requester.DEFAULT_LOCALE;
        }

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        errorPropertiesMap.remove("status");
        errorPropertiesMap.remove("error");
        errorPropertiesMap.put("code", exceptionCode);
        errorPropertiesMap.put("msg", i18nErrorMessage(locale, exceptionCode, args));
        errorPropertiesMap.put("data", data);

        if (log.isTraceEnabled()) {
            log.error("Handled request '{} {}' error: {}", request.method(), request.path(), throwable.getMessage(), throwable);
        } else {
            log.error("Handled request '{} {}' error: {}", request.method(), request.path(), throwable.getMessage());
        }

        return ServerResponse.status(message.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }

    protected ErrorMessage resolveErrorMessage(Exception e) {
        if (e instanceof DomainException de) {
            return new ErrorMessage(de.getExceptionCode(),
                    new CodeAndMessage(de.getExceptionCode().code(), de.getMessage()), de.getArgs());
        } else if (e instanceof BindException ex) {
            // 方法参数校验失败
            if (ex.getBindingResult().hasErrors()) {
                Optional<ObjectError> firstError = ex.getBindingResult().getAllErrors().stream().findFirst();
                if (firstError.isPresent()) {
                    return new ErrorMessage(ExceptionCode.BadRequest,
                            new CodeAndMessage("IllegalArgument", firstError.get().getDefaultMessage()));
                }
            }
        } else if (e instanceof IllegalArgumentException) {
            // 参数错误
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof IllegalStateException) {
            // 不适当的状态
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalState", e.getMessage()));
        } else if (e instanceof ConstraintViolationException) {
            // 违反约束条件
            StringBuilder builder = new StringBuilder("[");
            ((ConstraintViolationException) e).getConstraintViolations()
                    .forEach(violation -> builder.append(violation.getMessage()).append(","));
            String message = builder.substring(0, builder.length() - 1) + "]";
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", message));
        } else if (e instanceof ValidationException) {
            // 参数校验异常
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof HttpMessageNotReadableException) {
            // 参数校验异常（枚举字段）
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof WebExchangeBindException ex) {
            String errors = ex.getBindingResult().getAllErrors().stream()
                    .filter(Objects::nonNull)
                    .map(error -> {
                        final var errorMessage = new StringBuilder();
                        if (error instanceof FieldError fe) {
                            errorMessage.append(fe.getField()).append(" - ");
                        }
                        errorMessage.append(error.getDefaultMessage());
                        return errorMessage.toString();
                    })
                    .collect(Collectors.joining("\n"));
            return new ErrorMessage(ExceptionCode.BadRequest, new CodeAndMessage("IllegalArgument", errors));
        } else if (e instanceof ErrorResponseException ex) {
            HttpStatus status = (HttpStatus) ex.getStatusCode();
            ExceptionCode exceptionCode;
            if (status.is4xxClientError()) {
                exceptionCode = ExceptionCode.BadRequest;
            } else {
                exceptionCode = ExceptionCode.InternalServerError;
            }
            return new ErrorMessage(status, exceptionCode,
                    new CodeAndMessage(status.name(), ex.getMessage()));
        }

        log.error("GlobalHandler-未知异常", e);
        return new ErrorMessage(ExceptionCode.InternalServerError, new CodeAndMessage("InternalServerError", e.getMessage()));
    }

    private String i18nErrorMessage(Locale locale, ExceptionCode exceptionType, Object... args) {
        try {
            return messageSource.getMessage(exceptionType.code(), args, locale);
        } catch (NoSuchMessageException e) {
            log.warn(">>>>>> Exception code '{}' i18n message undefined.", exceptionType.code());
            return exceptionType.code();
        }
    }

    public record CodeAndMessage(String code, String msg) {
    }
}
