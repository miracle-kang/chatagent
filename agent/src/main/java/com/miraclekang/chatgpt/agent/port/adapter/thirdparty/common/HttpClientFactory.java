package com.miraclekang.chatgpt.agent.port.adapter.thirdparty.common;

import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.time.Duration;

public class HttpClientFactory {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final SSLContext sslContext;

    static {
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, new SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpClient newHttpClient() {
        return newHttpClient(null);
    }

    public static HttpClient newHttpClient(String proxy) {
        return newHttpClient(proxy, DEFAULT_TIMEOUT);
    }

    public static HttpClient newHttpClient(String proxy, Duration timeout) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(timeout);
        if (StringUtils.isNotBlank(proxy) && !proxy.trim().equals("none")) {
            builder.proxy(parseProxySelector(proxy));
        }
        return builder.build();
    }

    public static ProxySelector parseProxySelector(String proxy) {
        if (StringUtils.isBlank(proxy) || "none".equals(proxy)) {
            return ProxySelector.getDefault();
        }

        String host;
        int port;
        if (proxy.startsWith("^(http|https|socks)://")) {
            URI proxyURI = URI.create(proxy);
            String scheme = proxyURI.getScheme();
            if (scheme != null && !("http".equals(scheme) || "https".equals(scheme))) {
                throw new IllegalArgumentException("Unsupported proxy scheme " + scheme);
            }
            host = proxyURI.getHost();
            port = proxyURI.getPort();
        } else {
            int separate = proxy.lastIndexOf(':');
            separate = separate == -1 ? proxy.length() : separate;
            host = proxy.substring(0, separate);
            port = separate == proxy.length() ? 3128 : Integer.parseInt(proxy.substring(separate + 1));
        }
        return ProxySelector.of(new InetSocketAddress(host, port));
    }

    public static Proxy parseProxy(String proxy) {
        if (StringUtils.isBlank(proxy) || "none".equals(proxy)) {
            return Proxy.NO_PROXY;
        }

        String host;
        int port;
        Proxy.Type proxyType;
        if (proxy.startsWith("^(http|https|socks)://")) {
            URI proxyURI = URI.create(proxy);
            String scheme = proxyURI.getScheme();

            host = proxyURI.getHost();
            port = proxyURI.getPort();
            proxyType = "socks".equals(scheme) ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
        } else {
            int separate = proxy.lastIndexOf(':');
            separate = separate == -1 ? proxy.length() : separate;

            host = proxy.substring(0, separate);
            port = separate == proxy.length() ? 3128 : Integer.parseInt(proxy.substring(separate + 1));
            proxyType = Proxy.Type.HTTP;
        }
        return new Proxy(proxyType, new InetSocketAddress(host, port));
    }
}
