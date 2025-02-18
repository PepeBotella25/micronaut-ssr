package com.poc.micronaut.ssr;

import org.graalvm.polyglot.Value;
import reactor.core.publisher.Mono;

public class Promise implements PromiseExecutor {
    private final Mono<?> mono;

    Promise(Mono<?> mono) {
        this.mono = mono;
    }

    public void onPromiseCreation(Value resolve, Value reject) {
        mono.doOnSuccess(s -> resolve.executeVoid(s))
                .doOnError(reject::executeVoid)
                .subscribe();
    }
}
