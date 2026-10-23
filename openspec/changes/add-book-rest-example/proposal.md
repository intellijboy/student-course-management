# Proposal

## Why

工程骨架已完成（Spring Boot 4.1.2 + Gradle 9.8.2 + 国内镜像，空壳可跑），但目前只有一个 `GET /demo` 字符串端点。需要一个 RESTful 简单入门示例，作为业务开发的起点，同时演示 Spring Boot REST 的常见手法。

## What Changes

- 新增 Book 资源的 RESTful CRUD 示例（`/api/books`）：
  - `GET /api/books` 返回书目列表。
  - `GET /api/books/{id}` 返回单个书目，不存在时返回 404。
  - `POST /api/books` 创建书目，返回 201。
  - `PUT /api/books/{id}` 更新书目，不存在时返回 404。
  - `DELETE /api/books/{id}` 删除书目，不存在时返回 404，成功返回 204。
- 使用内存存储（无数据库依赖）。
- 保留现有 `GET /demo` 空壳端点不变。
- 为新增端点补充测试。

## Capabilities

### New Capabilities
<!-- 无新 capability -->

### Modified Capabilities
- `build-infrastructure`: 新增 Book CRUD REST 端点需求（在其既有 4 条需求之外）。

## Impact

- **代码**：新增 Book 模型（`cn.intellijboy.demo.domain`）、Book CRUD Controller（`cn.intellijboy.demo.web`）、内存存储仓库，以及对应 MockMvc 测试。
- **配置**：无新增依赖、无构建配置变更。
- **API**：新增 `/api/books` 一组 REST 端点；`/demo` 行为不变。
- **系统**：`openspec/specs/build-infrastructure/spec.md` 在归档时合并新增需求。
