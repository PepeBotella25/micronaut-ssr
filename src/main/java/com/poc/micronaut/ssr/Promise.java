package com.poc.micronaut.ssr;

import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.Introspected;
import org.graalvm.polyglot.Value;
import reactor.core.publisher.Mono;

@FunctionalInterface
interface PromiseExecutor {
    void onPromiseCreation(Value onResolve, Value onReject);
}

@Introspected
public class Promise implements PromiseExecutor {
    private final Mono<?> mono;

    Promise(Mono<?> mono) {
        this.mono = mono;
    }

    @Executable
    public void onPromiseCreation(Value resolve, Value reject) {
        mono.doOnSuccess(resolve::executeVoid).doOnError(reject::executeVoid).subscribe();
    }
}
