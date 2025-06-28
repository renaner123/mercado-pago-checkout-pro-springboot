module.exports = {
    moduleFileExtensions: ['js', 'vue', 'json'],
    transform: {
        '^.+\\.vue$': 'vue-jest',
        '^.+\\.js$': 'babel-jest',
        '^.+\\.mjs$': 'babel-jest'
    },
    testEnvironment: 'jsdom',
    transformIgnorePatterns: [
        '/node_modules/(?!vee-validate).+\\.js$'
    ]
};