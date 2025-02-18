package com.poc.micronaut.ssr;

import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

@Context
@Singleton
public class Utils {

    private final static String API_URL = "http://localhost:8080/";
    private static HttpClient client;

    @Inject
    Utils(@Client HttpClient client) {
        Utils.client = client;
    }

    public static Promise fetch(String url) {
        var internalRequest = HttpRequest
                .create(HttpMethod.GET, API_URL + url)
                .accept(MediaType.TEXT_PLAIN);
        return new Promise(Mono.from(client.exchange(internalRequest))
                .map(response -> response.body().toString(Charset.defaultCharset())));
    }
}
