## Micronaut SSR PoC

This PoC tests how to use `micronaut-views-react` package to server-side render (SSR) a `Preact` view.
The case covered here is where the `Preact` view depends on some async operations, like requesting some data, before being able to get rendered.

To do so, we configure a `render-script` that will do a request, wait for to finish and then passing the response data to the `Preact` as a prop. 

The Micronaut `ViewFilter` implementation seems to not support this use case as it tries to "write" the response right away using the rendered view without "waiting" for it to finish.
```
Writable writable = optionalViewsRenderer.get().render(view, modelAndView.getModel().orElse(null), request);
response.contentType(type);
response.body(writable);
```

To workaround that, we implemented our custom `ViewFilter` (`MyViewFilter`) that mimics the original one, but adds a `Publisher` to "wait" for the view render to finish before generate the response.

An [enhancement issue](https://github.com/micronaut-projects/micronaut-views/issues/857) has been created to see whether something like this workaround could work as a solution for this problem.

### Available endpoints
Uses `http://localhost:8080`.

- `/hello/working`
  - Does not use `@View`, but returns a `ModelAndView` instance to avoid default `ViewFilter` and use the custom `MyViewFilter` with the workaround
  - This endpoint produces the expected HTML
- `/hello/broken`
  - Uses the `@View` notation to use the default `ViewFilter`
  - This endpoint produces and blank page as the response is generated before the view rendering has finished.
- `/hello/data`
  - Called by the `Preact` view to generate the async rendering case that want to test

### JavaScript code

The JavasScript code is located in `src/frontend` and gets bundled automatically before java code is compiled.
The outcome `render-script.mjs` bundle is located in `src/frontend/build`.

#### Async Request

In order to be able to do the request to the `/hello/data` endpoint, the JavaScript code uses the polyglot to get an instance of a `Utils` java class that does the internal request using `HttpClient`. 