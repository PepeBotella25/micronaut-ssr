import { h } from "preact";
import render from "preact-render-to-string";
import { App } from "./App";
import { fetch } from "./utils";

export async function ssr(_component, props, callback) {
    const { name } = props;

    const data = await fetch({ url: "hello/data" });
    const ssr = render(h(App, { name, data }));

    callback.write(ssr);
}