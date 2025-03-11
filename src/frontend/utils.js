function getSsrUtils() {
    return Java.type("com.poc.micronaut.ssr.Utils");
}

export const fetch = async ({ url }) => {
    const { fetch } = getSsrUtils();
    const { onPromiseCreation } = fetch(url);
    return new Promise(onPromiseCreation);
};