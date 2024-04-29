package com.miraclekang.chatgpt.common.reactive;

import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReactiveUtils {

    /**
     * Blocking operation executor
     */
    private static final ExecutorService blockingExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public static <T> Mono<T> blockingOperation(Callable<T> callable) {
        return Requester.currentRequester().flatMap(requester ->
                Mono.create(sink -> blockingExecutor.submit(() -> {
                    try {
                        Requester.setBlockingRequester(requester);
                        T res = callable.call();
                        sink.success(res);
                    } catch (Exception ex) {
                        sink.error(ex);
                    } finally {
                        Requester.clearBlockingRequester();
                    }
                })));
    }

    public static Mono<Void> blockingOperation(Runnable runnable) {
        return Requester.currentRequester().flatMap(requester ->
                Mono.create(sink -> blockingExecutor.submit(() -> {
                    try {
                        Requester.setBlockingRequester(requester);
                        runnable.run();
                        sink.success();
                    } catch (Exception ex) {
                        sink.error(ex);
                    } finally {
                        Requester.clearBlockingRequester();
                    }
                })));
    }
}
