package com.poc.micronaut.ssr;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import reactor.core.publisher.Mono;

import java.util.Random;

@Controller("/hello")
public class MyController {

    @Produces(MediaType.TEXT_HTML)
    @Get("/working")
    public Mono<ModelAndView<SsrData>> working() {
        // Not using @View to make it go through the "workaround-ed" MyViewFilter
        // and not through micronaut ViewFilter
        return Mono.just(new ModelAndView<>("App", new SsrData("The Name")));
    }

    @Produces(MediaType.TEXT_HTML)
    @Get("/broken")
    @View("App")
    public Mono<SsrData> broken() {
        // Using @View to go through micronaut ViewFilter and not through MyViewFilter
        return Mono.just(new SsrData("The Name"));
    }

    @Produces(MediaType.TEXT_PLAIN)
    @Get("/data")
    public Mono<String> data() {
        return Mono.just("Some random data " + new Random().nextInt());
    }
}

@Introspected
record SsrData(String name) {}