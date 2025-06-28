const { defineConfig } = require('@vue/cli-service')
const {readFileSync} = require("node:fs");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    headers: {
      'Cross-Origin-Opener-Policy': 'same-origin-allow-popups',
      'Referrer-Policy': 'strict-origin-when-cross-origin',
    },
    server: {
      type: 'https',
      options: {
        key: readFileSync('./certs/example.com+5-key.pem'),
        cert: readFileSync('./certs/example.com+5.pem'),
      },
    },
  }
})