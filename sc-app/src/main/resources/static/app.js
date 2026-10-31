// ============================================
// 学生 / 课程 / 成绩 管理系统前端 —— 入口模块
// 基于 Vue3（全局构建版）+ 原生 ES 模块化组件
// ============================================

import AppComponent from './modules/AppComponent.js';

const { createApp } = Vue;

createApp(AppComponent).mount('#app');
