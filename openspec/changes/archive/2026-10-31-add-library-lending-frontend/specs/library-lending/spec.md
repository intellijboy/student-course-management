# Spec Delta

## ADDED Requirements

### Requirement: 浏览器管理页面
系统 SHALL 提供浏览器端页面，覆盖书目馆藏、借阅、归还、预约与查询的全部功能，页面通过后端 REST API 交互。

#### Scenario: 页面访问与功能可用
- **WHEN** 用户在浏览器打开管理页面并进入图书借阅页签
- **THEN** 页面可完成图书登记、图书与馆藏状态查询、借阅、归还、预约以及逾期查询操作