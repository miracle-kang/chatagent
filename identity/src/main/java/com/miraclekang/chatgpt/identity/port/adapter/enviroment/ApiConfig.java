package com.miraclekang.chatgpt.identity.port.adapter.enviroment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
public class ApiConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .displayName("Admin API")
                .packagesToScan("com.miraclekang.chatgpt.identity.port.adapter.restapi.admin")
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    @Bean
    public GroupedOpenApi internalApi() {
        return GroupedOpenApi.builder()
                .group("internal")
                .displayName("Internal API")
                .packagesToScan("com.miraclekang.chatgpt.identity.port.adapter.restapi.internal")
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder()
                .group("open")
                .displayName("Open API")
                .packagesToScan("com.miraclekang.chatgpt.identity.port.adapter.restapi.open")
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    private OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi
                .schemaRequirement("BearerJWT",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("BearerJWT", List.of("read", "write")));
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .modules(Arrays.asList(
                        new ParameterNamesModule(),
                        new Jdk8Module(),
                        new JavaTimeModule()))
                .deserializers(
                        zonedDateTimeJsonDeserializer(),
                        new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE),
                        new LocalTimeDeserializer(DateTimeFormatter.ISO_LOCAL_TIME),
                        new YearMonthDeserializer(DateTimeFormatter.ofPattern("yyyy-MM")))
                .serializers(
                        zonedDateTimeJsonSerializer(),
                        new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE),
                        new LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME),
                        new YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")))
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .serializersByType(Map.of(BigDecimal.class, BigDecimalToStringSerializer.instance));
    }

    private JsonDeserializer<LocalDateTime> localDateTimeJsonDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String txt = p.getText();
                if (txt == null || txt.isBlank()) {
                    return null;
                }
                try {
                    return LocalDateTime.parse(txt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception ex) {
                    throw new IOException("时间格式错误", ex);
                }
            }

            @Override
            public Class<LocalDateTime> handledType() {
                return LocalDateTime.class;
            }
        };
    }

    private JsonSerializer<LocalDateTime> localDateTimeJsonSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    return;
                }
                try {
                    gen.writeString(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value));
                } catch (Exception ex) {
                    gen.writeString("");
                }
            }

            @Override
            public Class<LocalDateTime> handledType() {
                return LocalDateTime.class;
            }
        };
    }

    private JsonDeserializer<ZonedDateTime> zonedDateTimeJsonDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                Number number = p.getNumberValue();
                if (number == null || number.longValue() == 0) {
                    return null;
                }
                try {
                    Instant instant = Instant.ofEpochMilli(number.longValue());
                    return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
                } catch (Exception ex) {
                    throw new IOException("时间格式错误", ex);
                }
            }

            @Override
            public Class<ZonedDateTime> handledType() {
                return ZonedDateTime.class;
            }
        };
    }

    private JsonSerializer<ZonedDateTime> zonedDateTimeJsonSerializer() {
        return new JsonSerializer<>() {
            @Override
            public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    return;
                }
                try {
                    gen.writeNumber(value.toInstant().toEpochMilli());
                } catch (Exception ex) {
                    gen.writeNull();
                }
            }

            @Override
            public Class<ZonedDateTime> handledType() {
                return ZonedDateTime.class;
            }
        };
    }

    @JacksonStdImpl
    static class BigDecimalToStringSerializer extends ToStringSerializer {
        public final static BigDecimalToStringSerializer instance = new BigDecimalToStringSerializer();

        public BigDecimalToStringSerializer() {
            super(Object.class);
        }

        public BigDecimalToStringSerializer(Class<?> handledType) {
            super(handledType);
        }

        @Override
        public boolean isEmpty(SerializerProvider prov, Object value) {
            if (value == null) {
                return true;
            }
            String str = ((BigDecimal) value).toPlainString();
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(((BigDecimal) value).toPlainString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen,
                                      SerializerProvider provider, TypeSerializer typeSer)
                throws IOException {
            // no type info, just regular serialization
            serialize(value, gen, provider);
        }
    }
}
