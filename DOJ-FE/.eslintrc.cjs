module.exports = {
    root: true,
    'extends': [
      'plugin:vue/vue3-essential',
      'eslint:recommended',
      'plugin:vue/vue3-strongly-recommended', // 增加 Vue3 严格模式
      'plugin:vue/vue3-recommended', // 增加 Vue3 严格推荐模式
    ],
    parserOptions: {
      ecmaVersion: 'latest'
    },
    rules: {
      'vue/multi-word-component-names': 0, // 保持允许单词组件名
      'no-unused-vars': 'error', // 严格要求不能有未使用的变量
      'eqeqeq': 'error', // 要求使用 === 和 !==
    //   'curly': 'error', // 要求始终使用大括号
    //   'no-console': 'warn', // 警告console语句
    //   'no-debugger': 'error', // 禁用 debugger 语句
      'strict': ['error', 'global'], // 启用严格模式
    },
  }
  