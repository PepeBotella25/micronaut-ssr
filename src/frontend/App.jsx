export function App(props) {
    const { name, data } = props;

    return (
        <html>
            <head>
                <title>Micronaut SSR Test</title>
            </head>
            <body>
                <div>Hello {name}!</div>
                {data && <div>Data: {data}</div>}
            </body>
        </html>
    );
}