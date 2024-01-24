package com.miraclekang.chatgpt.subscription.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miraclekang.chatgpt.subscription.domain.model.equity.ChatModel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public interface Converters {

    @Converter
    class DecimalListConverter implements AttributeConverter<List<BigDecimal>, String> {

        @Override
        public String convertToDatabaseColumn(List<BigDecimal> attribute) {
            if (attribute.isEmpty()) {
                return "";
            }
            return attribute.stream().map(BigDecimal::toPlainString)
                    .collect(Collectors.joining(","));
        }

        @Override
        public List<BigDecimal> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return new ArrayList<>();
            }

            // format: Type:[a,b,c]
            return Arrays.stream(dbData.split(",")).map(BigDecimal::new)
                    .collect(Collectors.toList());
        }

    }

    @Converter
    class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            if (attribute == null || attribute.isEmpty()) {
                return "";
            }
            return String.join(",", attribute);
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return new LinkedList<>();
            }
            return Arrays.stream(dbData.split(",")).collect(Collectors.toCollection(LinkedList::new));
        }
    }

    @Converter
    class StringMapConverter implements AttributeConverter<Map<String, String>, String> {

        private final ObjectMapper objectMapper;

        public StringMapConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String convertToDatabaseColumn(Map<String, String> attribute) {
            if (attribute == null) {
                return "";
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, String> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return new LinkedHashMap<>();
            }
            try {
                return objectMapper.readValue(dbData, new TypeReference<LinkedHashMap<String, String>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class IntegerMapConverter implements AttributeConverter<Map<String, Integer>, String> {
        private final ObjectMapper objectMapper;

        public IntegerMapConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String convertToDatabaseColumn(Map<String, Integer> attribute) {
            if (attribute == null) {
                return "";
            }
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Map<String, Integer> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return new LinkedHashMap<>();
            }
            try {
                return objectMapper.readValue(dbData, new TypeReference<LinkedHashMap<String, Integer>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ChatModelListConverter implements AttributeConverter<List<ChatModel>, String> {

        @Override
        public String convertToDatabaseColumn(List<ChatModel> attribute) {
            return attribute == null ? "" : attribute.stream().map(ChatModel::name)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
        }

        @Override
        public List<ChatModel> convertToEntityAttribute(String dbData) {
            if (StringUtils.isBlank(dbData)) {
                return new ArrayList<>();
            }
            return Arrays.stream(dbData.split(",")).map(ChatModel::valueOf)
                    .collect(Collectors.toList());
        }
    }
}
