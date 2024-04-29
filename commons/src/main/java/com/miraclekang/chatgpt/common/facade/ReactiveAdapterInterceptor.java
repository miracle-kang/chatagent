package com.miraclekang.chatgpt.common.facade;

import com.miraclekang.chatgpt.common.reactive.Requester;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.tracing.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Set;

@Slf4j
public class ReactiveAdapterInterceptor implements RequestInterceptor {

    private static final Set<String> HEADERS_WHITELIST = Set.of(
            "accept-language",
            "authorization",
            "cache-control",
            "x-real-ip",
            "x-forwarded-for",
            "x-forwarded-host",
            "x-forwarded-port",
            "x-request-id",
            "x-b3-traceid",
            "x-b3-spanid",
            "x-b3-parentspanid",
            "x-b3-sampled",
            "x-b3-flags"
    );

    @Override
    public void apply(RequestTemplate template) {

        Requester requester = Requester.blockingRequester();
        if (requester == null) {
            log.warn("Requester is not available, skip headers propagation");
            return;
        }

        // WebFlux headers
        if (requester.getExchange() != null) {
            ServerWebExchange exchange = requester.getExchange();
            exchange.getRequest().getHeaders().forEach((k, v) -> {
                if (HEADERS_WHITELIST.contains(k.toLowerCase())) {
                    template.header(k, v);
                }
            });
        }

        // Tracing headers
        if (requester.getTraceContext() != null) {
            TraceContext traceContext = requester.getTraceContext();
            template.header("X-B3-TraceId", traceContext.traceId());
            template.header("X-B3-SpanId", traceContext.spanId());
            template.header("X-B3-ParentSpanId", traceContext.parentId());
            template.header("X-B3-Sampled", BooleanUtils.isTrue(traceContext.sampled()) ? "1" : "0");
            template.header("X-B3-Flags", log.isDebugEnabled() ? "1" : "0");
        }
        log.debug("Request to {} propagation headers: {}", template.url(), template.headers());
    }
}
