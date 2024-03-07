package com.miraclekang.chatgpt.common.reactive;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ReactiveUtils {

    /**
     * Blocking operation executor
     */
    private static final ExecutorService blockingExecutor = ForkJoinPool.commonPool();

    public static <T> Mono<T> blockingOperation(Callable<T> callable) {
        return Mono.create(sink -> blockingExecutor.submit(() -> {
            try {
                T res = callable.call();
                sink.success(res);
            } catch (Exception ex) {
                sink.error(ex);
            }
        }));
    }

    public static Mono<Void> blockingOperation(Runnable runnable) {
        return Mono.create(sink -> blockingExecutor.submit(() -> {
            try {
                runnable.run();
                sink.success();
            } catch (Exception ex) {
                sink.error(ex);
            }
        }));
    }
}
