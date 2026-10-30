# Design

## Context

见 `proposal.md - Why`。前一变更 `remove-book-rest-example` 已移除 Book 示例并同步主 spec，`web` 包现仅剩 `studentcourse` 子包与 `DemoController`。`DemoController` 是主 spec「空壳应用启动与 HTTP 响应」需求的唯一实现，其验证锚点是 `StudentCourseManagementApplicationTests.demoEndpointReturnsSuccess`；全源码 grep 确认无其他引用（`application.yml` 的 `jdbc:h2:mem:demo` 为数据库名，无关）。删除是纯移除操作，不涉及数据迁移或运行时状态。

## Goals / Non-Goals

**Goals:**
- 删除 `/demo` 端点与 `DemoController`，`web` 包不再存在任何业务无关的示例类。
- 清理 `StudentCourseManagementApplicationTests` 中对 `/demo` 的测试与无用导入，`contextLoads` 保留。
- spec 与代码一致：移除「空壳应用启动与 HTTP 响应」需求，以「应用可启动」承接启动验证能力，并同步 Purpose 表述。
- 删除后 `./gradlew build` 全绿，学生课程成绩模块不受影响。

**Non-Goals:**
- 不修改 `student-course-management` 的任何需求与代码。
- 不修改 `openspec/changes/archive/` 下任何历史归档。
- 不重构便携分发包、CI 矩阵等其余 `build-infrastructure` 需求。

## Decisions

- **删除文件清单**：`src/main/java/cn/intellijboy/demo/web/DemoController.java` 直接删除；`StudentCourseManagementApplicationTests` 仅删 `demoEndpointReturnsSuccess` 方法及不再使用的 `get`、`content` 静态导入，`contextLoads` 原样保留。备选「保留 /demo 作为健康检查」被否：`/demo` 并非健康检查端点，删除后仍有业务端点可供验证。
- **spec 处理方式**：以 delta 的 `## REMOVED Requirements` 移除「空壳应用启动与 HTTP 响应」（含完整原块、Reason 与 Migration），同 delta `## ADDED Requirements` 新增「应用可启动」（仅保留启动与绑定端口场景，锚定 `contextLoads`）。理由：`/demo` 移除后「空壳应用」概念不复存在，原需求的「HTTP 请求得到响应」场景失去实现；但「应用可启动」是便携分发包与 CI 构建的前置冒烟能力，值得保留独立需求。备选「整条 REMOVED 不 ADDED」被否：会丢失启动验证的规格锚点；备选「RENAMED + MODIFIED」被否：sync 语义组合易产生歧义。
- **Purpose 同步**：主 spec Purpose 中「空壳应用的启动与 HTTP 响应能力」提法随需求替换更新。按 OpenSpec 惯例，已有 capability 的 Purpose 不经 delta 修改，在 apply 阶段 sync 后直接编辑主 spec。
- **保留 archive**：历史归档中提及 `/demo` 属历史快照，不改。

## Risks / Trade-offs

- [主 spec Purpose 与 delta 不同步] → apply 阶段 sync 后立即编辑主 spec Purpose，并以 `openspec validate --specs` 复核。
- [测试精简遗漏无用 import 导致编译告警] → apply 阶段 `./gradlew build` 作为验证兜底。
- [前一变更尚未归档，两变更并存] → `remove-book-rest-example` 的 delta 已同步至主 spec，归档时 REMOVED 重复应用会被跳过并警告，幂等无害；建议先归档前一变更再实施本变更。

## Migration Plan

无数据迁移。删除代码与同步 spec 后，以 `./gradlew build`（含测试）与 `openspec validate` 验证。回滚方式：恢复 `DemoController.java` 与测试方法、还原主 spec 的需求与 Purpose 表述。
