const path = require("path");

module.exports = {
    entry: ["./index.js"],
    output: {
        path: path.resolve(__dirname, "build"),
        filename: "render-script.mjs",
        module: true,
        library: {
            type: "module"
        },
        // GraalJS uses `globalThis` instead of `window` for the global object.
        globalObject: 'globalThis'
    },
    resolve: {
        extensions: [".js", ".jsx"]
    },
    devtool: false,
    experiments: {
        outputModule: true
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react'],
                        plugins: [
                            ["@babel/plugin-transform-react-jsx", { runtime: "automatic", importSource: "preact" }]
                        ]
                    }
                }
            }
        ]
    }
};