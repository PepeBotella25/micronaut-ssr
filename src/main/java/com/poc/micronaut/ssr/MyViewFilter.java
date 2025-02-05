package com.poc.micronaut.ssr;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.views.*;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Optional;

/**
 * This filter was based on micronaut-views ViewFilter
 */
@Requires(beans = ViewsResolver.class)
@Filter(Filter.MATCH_ALL_PATTERN)
public class MyViewFilter implements HttpServerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MyViewFilter.class);

    private static final MediaType UTF8_HTML = new MediaType(MediaType.TEXT_HTML, Map.of(MediaType.CHARSET_PARAMETER, "UTF-8"));

    private final ViewsResolver viewsResolver;
    private final ViewsRendererLocator viewsRendererLocator;
    private final ViewsModelDecorator viewsModelDecorator;

    @Inject
    MyViewFilter(ViewsResolver viewsResolver, ViewsRendererLocator viewsRendererLocator, ViewsModelDecorator viewsModelDecorator) {
        this.viewsResolver = viewsResolver;
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
    }

    @Override
    public final Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                            ServerFilterChain chain) {
        return Flux.from(chain.proceed(request))
                .switchMap(response -> {
                    Object body = response.body();

                    String view = viewsResolver.resolveView(request, response).orElse(null);
                    if (view == null || !view.equals("App")) {
                        if(view != null) {
                            LOG.info(String.format("Skipping MyViewFilter due to view name \"%s\" is not \"App\" for %s", view, request.getPath()));
                        }
                        return Flux.just(response);
                    }

                    MediaType type = UTF8_HTML;
                    Optional<ViewsRenderer> optionalViewsRenderer = viewsRendererLocator.resolveViewsRenderer(view, type.getName(), body);
                    if(optionalViewsRenderer.isEmpty()) {
                        return Flux.just(response);
                    }

                    LOG.info(String.format("Using MyViewFilter for %s", request.getPath()));

                    ModelAndView<?> modelAndView = new ModelAndView<>(view, body instanceof ModelAndView ? ((ModelAndView<?>) body).getModel().orElse(null) : body);
                    viewsModelDecorator.decorate(request, modelAndView);
                    Writable writable = optionalViewsRenderer.get().render(view, modelAndView.getModel().orElse(null), request);

                    // The idea here is to use a Publisher to "wait" for "write" to be called
                    // to write the body into the response.
                    // This code assumes "write" is called only once.
                    // Probably is not a safe assumption for the general case.
                    return Flux.from((Publisher<String>) subscriber -> {
                        subscriber.onSubscribe(new Subscription() {
                            @Override
                            public void request(long n)
                            {
                                try
                                {
                                    writable.writeTo(new Writer() {

                                        @Override
                                        public void write(char[] buffer, int off, int len) throws IOException
                                        {
                                            subscriber.onNext(String.valueOf(buffer));
                                            subscriber.onComplete();
                                        }

                                        @Override
                                        public void flush() throws IOException
                                        {
                                            subscriber.onComplete();
                                        }

                                        @Override
                                        public void close() throws IOException
                                        {
                                            subscriber.onComplete();
                                        }
                                    });
                                }
                                catch (Exception e)
                                {
                                    subscriber.onError(e);
                                }
                            }

                            @Override
                            public void cancel()
                            {
                                //subscriber.onComplete();
                            }
                        });
                    }).map(b -> response.body(b).contentType(type));
                });
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.RENDERING.order() + 1;
    }
}
