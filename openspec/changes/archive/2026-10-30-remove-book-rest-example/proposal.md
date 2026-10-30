# Proposal

## Why

工程已具备以 `student-course-management` capability 为代表的标准业务示例（经典 DDD 分层 + 真实持久化），`/api/books` 的 Book CRUD 只是早期 REST 入门脚手架，与工程当前定位重复且无业务价值。移除后代码库与规格回归到「学生课程成绩」单一业务模块，降低维护与理解成本。

## What Changes

- **BREAKING**：删除 Book CRUD REST 端点 `GET/POST/PUT/DELETE /api/books`，相应 404/201/204 行为一并移除。
- 删除 Book 示例代码：`web/Book.java`、`web/BookRequest.java`、`web/BookController.java`，以及 `web/BookControllerTests.java` 测试。
- 从 `build-infrastructure` capability 中移除 5 条 Book 需求（列表查询、单个查询、创建、更新、删除），使规格与代码一致。
- `/demo` 空壳端点与 `DemoController` 保持不变（属于 `build-infrastructure` 的「空壳应用启动与 HTTP 响应」需求）。
- 学生课程成绩模块（`student-course-management`）不受影响。

## Capabilities

### New Capabilities
<!-- 本变更不引入新 capability。 -->

### Modified Capabilities
- `build-infrastructure`: 删除 Book 列表查询、Book 单个查询、Book 创建、Book 更新、Book 删除 5 条需求；其余构建基础设施需求（Gradle Wrapper、国内镜像、Java 21 编译打包、空壳应用、便携分发包、CI 矩阵）不变。

## Impact

- **代码**：删除 `cn.intellijboy.demo.web` 包下的 `Book`、`BookRequest`、`BookController` 三个类及其对应测试 `BookControllerTests`；无其他代码引用，删除后不影响编译。
- **API**：`/api/books` 一组端点下线；`/demo` 与学生课程成绩相关端点行为不变。
- **规格**：`openspec/specs/build-infrastructure/spec.md` 移除 5 条 Book 需求（需求数由 12 减至 7）。
- **依赖/系统**：无新增依赖；不涉及持久化与数据迁移（Book 为内存存储示例）。
- **不在范围**：`openspec/changes/archive/2026-10-24-add-book-rest-example/` 等历史归档保留不动，作为审计记录。
