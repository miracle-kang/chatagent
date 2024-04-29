package com.miraclekang.chatgpt.common.facade;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.observation.Observation;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.handler.TracingObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.util.context.ContextView;

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

    private static final InheritableThreadLocal<ContextView>
            contextThreadLocal = new InheritableThreadLocal<>();

    public static void setContextView(ContextView contextView) {
        contextThreadLocal.set(contextView);
    }

    @Override
    public void apply(RequestTemplate template) {

        ContextView contextView = contextThreadLocal.get();
        if (contextView == null) {
            return;
        }

        // WebFlux headers
        if (contextView.hasKey(ServerWebExchange.class)) {
            ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
            exchange.getRequest().getHeaders().forEach((k, v) -> {
                if (HEADERS_WHITELIST.contains(k.toLowerCase())) {
                    template.header(k, v);
                }
            });
        }

        // Tracing headers
        if (contextView.hasKey("micrometer.observation")) {
            Observation observation = contextView.get("micrometer.observation");
            TracingObservationHandler.TracingContext tracingContext = observation.getContextView()
                    .get(TracingObservationHandler.TracingContext.class);
            if (tracingContext != null) {
                TraceContext traceContext = tracingContext.getSpan().context();
                template.header("X-B3-TraceId", traceContext.traceId());
                template.header("X-B3-SpanId", traceContext.spanId());
                template.header("X-B3-ParentSpanId", traceContext.parentId());
                template.header("X-B3-Sampled", BooleanUtils.isTrue(traceContext.sampled()) ? "1" : "0");
                template.header("X-B3-Flags", log.isDebugEnabled() ? "1" : "0");
            }
        }

        log.debug("Request to {} propagation headers: {}", template.url(), template.headers());
        contextThreadLocal.remove();
    }
}
