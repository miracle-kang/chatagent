package com.miraclekang.chatgpt.common.reactive;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

public class ReactiveUtils {

    public static <T> Mono<T> blockingOperation(Callable<T> callable) {
        return Mono.<T>create(sink -> {
                    try {
                        T res = callable.call();
                        sink.success(res);
                    } catch (Exception ex) {
                        sink.error(ex);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public static Mono<Void> blockingOperation(Runnable runnable) {
        return Mono.<Void>create(sink -> {
                    try {
                        runnable.run();
                        sink.success();
                    } catch (Exception ex) {
                        sink.error(ex);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
