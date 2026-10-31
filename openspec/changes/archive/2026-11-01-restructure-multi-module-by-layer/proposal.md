# Proposal

## Why

当前工程为单一 Gradle 模块：两个限界上下文（`librarylending`、`studentcourse`）各自的领域、应用、基础设施、接口层代码全部平铺在同一个 `src/` 下、打进同一个 JAR。随着类数量增长（约 90 个主类、40 个测试类），跨层、跨上下文代码调用容易混淆，分层边界失去物理约束，工程的可读性与可维护性下降。

将工程按 DDD 分层拆分为多 Gradle 子模块的模块化单体，用构建系统从物理上固化依赖方向，使每个层的职责清晰、跨层调用受限、可独立演进与测试。

## What Changes

- 将单一 Gradle 工程重构为**模块化单体**：`settings.gradle.kts` 声明多层子模块，一次构建仍产出（可部署的）完整单体应用，不拆分进程、不拆分数据库。
- 新增 `sc-common`（公共契约）模块：承载两个上下文共享的契约/常量/通用工具，作为 `sc-domain` 与其它层的跨模块基座。
- 按 DDD 层拆分源码：`sc-domain`（领域模型/仓储接口/事件/ACL 端口）、`sc-application`（应用服务/用例编排）、`sc-infrastructure`（JPA 实现/防腐层适配器/持久化）、`sc-interfaces`（web 控制器/请求响应 + 静态前端资源）各成 Gradle 子模块。
- 依赖方向由 Gradle 强制单向：`sc-interfaces → sc-application → sc-domain ← sc-infrastructure`，`sc-domain` 不依赖任何层；`sc-common` 被多模块共享。任何违反在构建期即失败。
- **BREAKING**（工程结构层面）：`settings.gradle.kts` 新增 `include` 多模块、各子模块新增 `build.gradle.kts`、源码从 `src/main/java/cn/intellijboy/demo/...` 迁至对应模块目录。包名（`cn.intellijboy.demo...`）保持不变，业务代码内容不变。
- 唯一跨上下文耦合点 `ReaderCatalogGatewayAdapter`（借阅→查学生）在拆分后仍以进程内方式保留于 `sc-infrastructure` 模块（模块化单体，不引入网络调用）。
- **不改变**任何 REST API、领域规则、数据库 schema 与运行行为；迁移为纯结构调整，测试语义与结果不变。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无 —— 本变更为纯工程结构重构，不产生任何 spec 级行为变化，故本变更声明 `skip_specs: true`。）

## Impact

- **构建结构**：`settings.gradle.kts` 增 `include` 子模块；新建各模块 `build.gradle.kts`；根构建保留 `student-course-management` 聚合。
- **受影响文件**：几乎所有 `src/main/java`、`src/test/java` 源文件按层迁移到子模块目录（包路径不变，仅物理目录移动）；`build.gradle.kts` 重写为逐模块依赖声明。
- **新增**：`sc-common` 模块及其构建配置。
- **依赖与资源**：静态前端（`src/main/resources/static/`）随 `sc-interfaces` 或单体聚合模块；`application.yml`、`db/schema.sql`、`db/data.sql` 移至最终的装配/启动模块（`sc-app`）。
- **行为变化**：无。构建产物体积、启动方式、API 契约均不变（模块化单体仍是单个可运行 Spring Boot 应用）。
- **不回退项**：本变更不引入网络级微服务拆分、不引入服务注册/网关、不拆分数据库（与用户既定决策一致：不分库、不拆进程）。