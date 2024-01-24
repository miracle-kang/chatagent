package com.miraclekang.chatgpt.assistant.port.adapter.thirdparty.common;

import feign.Headers;
import feign.jaxrs.JAXRSContract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static feign.Util.checkState;

public class ExJAXRSContract extends JAXRSContract {

    public ExJAXRSContract() {
        super.registerParameterAnnotation(ObjectParam.class,
                (annotation, metadata, paramIndex) -> metadata.queryMapIndex(paramIndex));

        super.registerClassAnnotation(Headers.class, (header, data) -> {
            final String[] headersOnType = header.value();
            checkState(headersOnType.length > 0, "Headers annotation was empty on type %s.",
                    data.configKey());
            final Map<String, Collection<String>> headers = toMap(headersOnType);
            headers.putAll(data.template().headers());
            data.template().headers(null); // to clear
            data.template().headers(headers);
        });

        super.registerMethodAnnotation(Headers.class, (header, data) -> {
            final String[] headersOnMethod = header.value();
            checkState(headersOnMethod.length > 0, "Headers annotation was empty on method %s.",
                    data.configKey());
            data.template().headers(toMap(headersOnMethod));
        });
    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        final Map<String, Collection<String>> result =
                new LinkedHashMap<String, Collection<String>>(input.length);
        for (final String header : input) {
            final int colon = header.indexOf(':');
            final String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<String>(1));
            }
            result.get(name).add(header.substring(colon + 1).trim());
        }
        return result;
    }
}
