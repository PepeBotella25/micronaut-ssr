package com.poc.micronaut.ssr;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Executable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;

import java.io.Closeable;
import java.io.IOException;

@Context
@Singleton
public class Utils implements Closeable {

    private static HttpClient httpClient;
    private static BlockingHttpClient client;

    Utils(@Client("/") HttpClient httpClient) {
        Utils.httpClient = httpClient;
        Utils.client = httpClient.toBlocking();
    }

    public static PromiseExecutor fetch(String url) {
        var request = HttpRequest.GET(url).accept(MediaType.TEXT_PLAIN);
        try {
            String response = client.retrieve(request);
            return new StringPromise(response);
        } catch (HttpClientResponseException e) {
            return new HttpClientResponseExceptionPromise(e);
        }
    }

    @Override
    public void close() throws IOException {
        client.close();
        httpClient.close();
    }
}
