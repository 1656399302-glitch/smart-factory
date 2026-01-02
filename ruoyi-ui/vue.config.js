"use strict";
const path = require("path");

function resolve(dir) {
  return path.join(__dirname, dir);
}

const CompressionPlugin = require("compression-webpack-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");

const name = process.env.VUE_APP_TITLE || "合作创新平台"; // 网页标题

const port = process.env.port || process.env.npm_config_port || 80; // 端口

// vue.config.js 配置说明
//官方vue.config.js 参考文档 https://cli.vuejs.org/zh/config/#css-loaderoptions
// 这里只列一部分，具体配置参考文档
module.exports = {
  // 部署生产环境和开发环境下的URL。
  // 默认情况下，Vue CLI 会假设你的应用是被部署在一个域名的根路径上
  // 例如 https://www.ruoyi.vip/。如果应用被部署在一个子路径上，你就需要用这个选项指定这个子路径。例如，如果你的应用被部署在 https://www.ruoyi.vip/admin/，则设置 baseUrl 为 /admin/。
  publicPath: process.env.NODE_ENV === "production" ? "/" : "/",
  // 在npm run build 或 yarn build 时 ，生成文件的目录名称（要和baseUrl的生产环境路径一致）（默认dist）
  outputDir: "dist",
  // 用于放置生成的静态资源 (js、css、img、fonts) 的；（项目打包之后，静态资源会放在这个文件夹下）
  assetsDir: "static",
  // 是否开启eslint保存检测，有效值：ture | false | 'error'
  lintOnSave: process.env.NODE_ENV === "development",
  // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
  productionSourceMap: false,
  transpileDependencies: ["quill"],
  // webpack-dev-server 相关配置
  devServer: {
    host: "0.0.0.0",
    port: port,
    open: false,
    proxy: {
      // detail: https://cli.vuejs.org/config/#devserver-proxy
      [process.env.VUE_APP_BASE_API]: {
        target: `http://localhost:8081`,
        changeOrigin: true,
        pathRewrite: {
          ["^" + process.env.VUE_APP_BASE_API]: "",
        },
      },
    },
    disableHostCheck: true,
    hotOnly: true, // 启用热模块替换
    inline: true, // 使用 inline 模式
  },
  css: {
    loaderOptions: {
      sass: {
        sassOptions: {
          outputStyle: "expanded",
          hot: true,
          sourceMap: true,
        },
      },
    },
  },
  configureWebpack: {
    name: name,
    devtool: "cheap-module-eval-source-map",
    resolve: {
      alias: {
        "@": resolve("src"),
      },
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            compress: {
              drop_console: true, // 移除 console
            },
          },
        }),
        new OptimizeCSSAssetsPlugin({}),
      ],
    },
    plugins: [
      new CompressionPlugin({
        cache: false,
        test: /\.(js|css|html|jpe?g|png|gif|svg)?$/i,
        filename: "[path][base].gz[query]",
        algorithm: "gzip",
        minRatio: 0.8,
        deleteOriginalAssets: false,
      }),
    ],
  },
  chainWebpack(config) {
    config.plugins.delete("preload"); // TODO: need test
    config.plugins.delete("prefetch"); // TODO: need test

    // set svg-sprite-loader
    config.module.rule("svg").exclude.add(resolve("src/assets/icons")).end();
    config.module
      .rule("icons")
      .test(/\.svg$/)
      .include.add(resolve("src/assets/icons"))
      .end()
      .use("svg-sprite-loader")
      .loader("svg-sprite-loader")
      .options({
        symbolId: "icon-[name]",
      })
      .end();

    // 配置 style-loader,开发环境下使用style-loader,生产环境下使用mini-css-extract-plugin
    if (process.env.NODE_ENV === "development") {
      config.module
        .rule("css")
        .oneOf("vue")
        .use("style-loader")
        .loader("style-loader")
        .before("css-loader")
        .end();
    }

    config.when(process.env.NODE_ENV !== "development", (config) => {
      config
        .plugin("ScriptExtHtmlWebpackPlugin")
        .after("html")
        .use("script-ext-html-webpack-plugin", [
          {
            // `runtime` must same as runtimeChunk name. default is `runtime`
            inline: /runtime\..*\.js$/,
          },
        ])
        .end();

      config.optimization.splitChunks({
        chunks: "all",
        cacheGroups: {
          libs: {
            name: "chunk-libs",
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: "initial",
          },
          elementUI: {
            name: "chunk-elementUI",
            test: /[\\/]node_modules[\\/]_?element-ui(.*)/,
            priority: 20,
          },
          commons: {
            name: "chunk-commons",
            test: resolve("src/components"),
            minChunks: 3,
            priority: 5,
            reuseExistingChunk: true,
          },
        },
      });
      config.optimization.runtimeChunk("single");
    });
  },
};
