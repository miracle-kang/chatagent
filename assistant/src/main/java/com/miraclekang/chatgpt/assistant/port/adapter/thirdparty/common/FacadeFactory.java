package com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.*;
import feign.codec.Decoder;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.http2client.Http2Client;
import feign.jackson.JacksonDecoder;
import feign.reactive.ReactorFeign;
import feign.slf4j.Slf4jLogger;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FacadeFactory {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    public static <T> T newFacade(Class<T> type, String baseUrl) {
        return newFacade(type, baseUrl, null);
    }

    public static <T> T newFacade(Class<T> type, String baseUrl, String proxy) {
        return newFacade(type, baseUrl, proxy, List.of());
    }

    public static <T> T newFacade(Class<T> type, String baseUrl, String proxy,
                                  List<RequestInterceptor> interceptors) {
        return newFacade(type, baseUrl, proxy, interceptors, null);
    }

    public static <T> T newFacade(Class<T> type, String baseUrl, String proxy,
                                  List<RequestInterceptor> interceptors, ResponseInterceptor responseInterceptor) {
        return newFacade(type, baseUrl, proxy, Duration.ofSeconds(30), interceptors, responseInterceptor);
    }

    public static <T> T newFacade(Class<T> type, String baseUrl, String proxy, Duration timeout,
                                  List<RequestInterceptor> interceptors, ResponseInterceptor responseInterceptor) {
        Feign.Builder feignBuilder = Feign.builder()
                .client(new Http2Client(HttpClientFactory.newHttpClient(proxy, timeout)))
                .options(new Request.Options(
                        timeout.toMillis(), TimeUnit.MILLISECONDS,
                        timeout.toMillis(), TimeUnit.MILLISECONDS,
                        true))
                .encoder(new CustomJacksonEncoder(DEFAULT_OBJECT_MAPPER))
                .decoder(new JacksonDecoder(DEFAULT_OBJECT_MAPPER))
                .logger(new Slf4jLogger(type))
                .logLevel(Logger.Level.BASIC)
                .contract(new ExJAXRSContract())
                .requestInterceptors(interceptors);
        if (responseInterceptor != null) {
            feignBuilder.responseInterceptor(responseInterceptor);
        }

        return feignBuilder.target(type, baseUrl);
    }

    public static <T> T newReactorFacade(Class<T> type, String baseUrl, String proxy, Duration timeout,
                                         List<RequestInterceptor> interceptors, ResponseInterceptor responseInterceptor) {
        Feign.Builder feignBuilder = ReactorFeign.builder()
                .client(new Http2Client(HttpClientFactory.newHttpClient(proxy, timeout)))
                .options(new Request.Options(
                        timeout.toMillis(), TimeUnit.MILLISECONDS,
                        timeout.toMillis(), TimeUnit.MILLISECONDS,
                        true))
                .encoder(new CustomJacksonEncoder(DEFAULT_OBJECT_MAPPER))
                .decoder(new EventStreamJacksonDecoder(DEFAULT_OBJECT_MAPPER))
                .logger(new Slf4jLogger(type))
                .logLevel(Logger.Level.BASIC)
                .contract(new ExJAXRSContract())
                .requestInterceptors(interceptors);
        if (responseInterceptor != null) {
            feignBuilder.responseInterceptor(responseInterceptor);
        }

        return feignBuilder.target(type, baseUrl);
    }

    static class CustomJacksonEncoder implements Encoder {

        private final ObjectMapper objectMapper;

        CustomJacksonEncoder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            if (object == null) {
                return;
            }
            try {
                JavaType javaType = objectMapper.getTypeFactory().constructType(bodyType);
                template.body(objectMapper.writerFor(javaType).writeValueAsBytes(object), StandardCharsets.UTF_8);
                template.header("Content-Type", "application/json; charset=utf-8");
            } catch (JsonProcessingException e) {
                throw new EncodeException(e.getMessage(), e);
            }
        }
    }

    static class EventStreamJacksonDecoder implements Decoder {

        private final ObjectMapper mapper;

        public EventStreamJacksonDecoder(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public Object decode(Response response, Type type) throws IOException {
            if (response.status() == 404 || response.status() == 204)
                return Util.emptyValueOf(type);
            if (response.body() == null)
                return null;

            try {
                return mapper.readValue(extraBody(response), mapper.constructType(type));
            } catch (RuntimeJsonMappingException e) {
                if (e.getCause() != null && e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }

        private String extraBody(Response response) throws IOException {
            String contentType = response.headers().get("Content-Type")
                    .stream().distinct().findAny().orElse(null);

            if (contentType != null && MediaType.TEXT_EVENT_STREAM.includes(MediaType.parseMediaType(contentType))) {
                Reader reader = response.body().asReader(response.charset());
                BufferedReader bufferedReader;
                if (!reader.markSupported()) {
                    bufferedReader = new BufferedReader(reader, 1);
                } else {
                    bufferedReader = new BufferedReader(reader);
                }
                // Read the first byte to see if we have any data
                bufferedReader.mark(1);
                if (bufferedReader.read() == -1) {
                    return null; // Eagerly returning null avoids "No content to map due to end-of-input"
                }
                bufferedReader.reset();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        return line.substring(6);
                    }
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(response.body().asInputStream(), bos);
            return bos.toString(response.charset());
        }
    }
}
