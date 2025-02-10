package com.poc.micronaut.ssr;

import io.micronaut.core.annotation.Introspected;
import org.graalvm.polyglot.Value;

public class StringPromise implements PromiseExecutor {
    private final String value;
    public StringPromise(String value) {
        this.value = value;
    }

    @Override
    public void onPromiseCreation(Value onResolve, Value onReject) {
        onResolve.execute(value);
    }
}
