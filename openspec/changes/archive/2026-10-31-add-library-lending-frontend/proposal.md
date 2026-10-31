# Proposal

## Why

`library-lending` 上下文的后端能力（书目馆藏、借阅、归还、预约、逾期、查询）已完整实现并通过 REST API 暴露，但没有任何浏览器端界面；现有前端仅覆盖「学生/课程/成绩」三块。为让图书借阅在各能力上与前两者对等、可演示，补齐其在现有单页应用中的前端界面。

## What Changes

- 在现有静态单页（`src/main/resources/static/`）顶部导航新增「图书借阅」标签页，覆盖 librarylending 全部 7 条能力的浏览器端操作：
  - 图书管理：登记图书、图书列表、查看馆藏状态与预约队列
  - 借阅：选择读者（来自学生）与图书后借阅
  - 归还：对在借记录执行归还
  - 预约：对副本全部被借出的图书创建预约
  - 查询：按读者查询借阅记录、逾期列表（含逾期费用）
- 单页标题与顶部标题由「学生课程管理系统」改为覆盖两个上下文的统一名称。
- 复用现有 `api.js` 请求封装与 `AppComponent` 的导航/通知机制。
- **不改变**任何后端 REST 端点、请求/响应结构、HTTP 行为；本次仅新增 `static/` 前端资源。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

- `library-lending`: 新增「浏览器管理页面」需求——系统 SHALL 提供浏览器端页面，覆盖书目馆藏、借阅、归还、预约、查询全部功能，页面通过后端 REST API 交互。该需求对齐 `student-course-management` 既有「浏览器管理页面」写法。

## Impact

- 受影响文件：`src/main/resources/static/index.html`、`src/main/resources/static/modules/AppComponent.js`、新增 `modules/BookList.js` 等前端组件。
- 复用接口：`/api/books`、`/api/loans`、`/api/reservations`、`/api/students`（作为读者下拉数据源）。
- 后端与构建：无改动、无新增依赖。