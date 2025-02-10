package com.poc.micronaut.ssr;

import org.graalvm.polyglot.Value;

@FunctionalInterface
public interface PromiseExecutor {
    void onPromiseCreation(Value onSuccess, Value onFailure);
}
