package com.poc.micronaut.ssr;

import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.graalvm.polyglot.Value;

public class HttpClientResponseExceptionPromise implements PromiseExecutor {
    private final HttpClientResponseException ex;

    public HttpClientResponseExceptionPromise(HttpClientResponseException ex) {
        this.ex = ex;
    }

    @Override
    public void onPromiseCreation(Value onResolve, Value onReject) {
        onReject.execute(ex.getMessage());
    }
}
