# Proposal

## Why

两个限界上下文的 `web` 层将 Controller、Request、View/DTO、ExceptionHandler 全部平铺在单一包内，且两个上下文风格不一致（librarylending 用聚合类 `Dtos.java`，studentcourse 用逐文件 `XxxDto`）。随着接口层元素增多，同一功能的实现分散导致定位成本上升；本次变更在体量尚小、行为零变化的前提下，一次性将接口层按功能/资源重组并统一风格。

## What Changes

- 将 `librarylending/web` 按功能拆分为 `book/`、`loan/`、`reservation/` 三个子包；聚合类 `Dtos.java` 拆除，其中的嵌套 record 提升为单文件顶层类（`BookView`、`BookCopyView`、`BookStatusView`、`LoanView`、`OverdueView`、`ReturnView`、`ReservationView`）。
- 将 `studentcourse/web` 按功能拆分为 `student/`、`course/`、`grade/` 三个子包。
- 统一命名：studentcourse 的 `XxxDto` 全部更名为 `XxxView`（`StudentDto` → `StudentView`、`CourseDto` → `CourseView`、`CourseGradeDto` → `CourseGradeView`）。
- 两个 ExceptionHandler（`LibraryLendingExceptionHandler`、`GlobalExceptionHandler`）作为横切关注点留在 `web` 根包，仅调整跨子包 import。
- 测试类（4 个跨资源集成/E2E 测试）留在 `src/test/.../web` 根包，仅同步 import 与更名引用。
- **不改变**：任何 REST 端点、URL、请求/响应结构、HTTP 状态码、模块行为。纯结构性重构。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无 —— 本次为纯代码结构重构，不产生任何 spec 级行为变化，故本变更声明 `skip_specs: true`。`library-lending` 与 `student-course-management` 的全部需求维持不变。）

## Impact

- 受影响代码：`src/main/java/cn/intellijboy/demo/librarylending/web/`（8 个文件重组）、`src/main/java/cn/intellijboy/demo/studentcourse/web/`（10 个文件重组）及对应 `src/test` 下的 4 个测试类。
- 对外影响：无。REST API 的 URL、方法、请求/响应体、状态码全部不变；Spring 组件扫描按包前缀 `cn.intellijboy.demo` 自动覆盖新子包，无需配置改动。
- 依赖与构建：无新增依赖，`build.gradle.kts` 不变。
