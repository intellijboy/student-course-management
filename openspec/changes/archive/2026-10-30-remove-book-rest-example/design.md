# Design

## Context

见 `proposal.md - Why`。当前工程 `web` 包下同时存在 Book CRUD 示例（`Book`/`BookRequest`/`BookController`）与 `studentcourse` 子包，二者相互独立、无任何代码引用关系；`openspec/specs/build-infrastructure/spec.md` 中 Book 的 5 条需求对应这套示例代码。删除是纯移除操作，不涉及数据迁移或运行时状态。

## Goals / Non-Goals

**Goals:**
- 删除 Book 示例的全部源码与测试，`web` 包不再存在 Book 相关类。
- 同步移除 `build-infrastructure` 主 spec 中的 5 条 Book 需求，保持规格与实现一致。
- 删除后 `./gradlew build` 全量编译与测试通过，学生课程成绩模块不受影响。

**Non-Goals:**
- 不删除 `/demo` 空壳端点与 `DemoController`（属于 `build-infrastructure` 的「空壳应用启动与 HTTP 响应」需求）。
- 不修改 `openspec/changes/archive/` 下任何历史归档（保留审计记录）。
- 不重构、不重命名 `studentcourse` 模块内任何代码。

## Decisions

- **删除文件清单**：`src/main/java/cn/intellijboy/demo/web/Book.java`、`BookRequest.java`、`BookController.java` 与 `src/test/java/cn/intellijboy/demo/web/BookControllerTests.java` 四个文件直接删除。理由：grep 全源码确认无任何外部引用，删除即完成，无需替换或迁移。备选「保留作为参考示例」被否：与「仅保留学生课程成绩模块」的目标冲突。
- **规格同步方式**：按 OpenSpec 惯例，当前变更的 delta spec（`specs/build-infrastructure/spec.md`）用 `## REMOVED Requirements` 声明删除，在 apply 阶段执行 `openspec sync specs` 写入主 spec。备选「直接改主 spec」被否：会绕过 OpenSpec 变更审计链。
- **保留 `/demo` 端点**：`StudentCourseManagementApplicationTests.demoEndpointReturnsSuccess` 与 `DemoController` 原样保留。理由：它们是 `build-infrastructure` capability 的需求实现，不属于 BookController 示例范畴。
- **保留 archive**：`openspec/changes/archive/2026-10-24-add-book-rest-example/` 不动。理由：归档是历史审计记录，OpenSpec 惯例不修改已归档变更。

## Risks / Trade-offs

- [archive 中仍提及 Book] → 归档属于历史快照，内容与当前代码不一致属预期，不影响运行时与规格，不做处理。
- [删除后仍有隐藏引用导致编译失败] → 已通过全源码 grep 确认 `src/` 内仅这 4 个文件引用 Book；apply 阶段以 `./gradlew build` 作为验证兜底。
- [主 spec 与 delta 不同步] → apply 阶段必须执行 `openspec sync specs`，并复核主 spec 需求数由 12 降至 7。

## Migration Plan

无数据迁移。删除代码与同步 spec 后，以 `./gradlew build`（含测试）与 `openspec validate` 验证即可。回滚方式：恢复被删除的 4 个文件、将主 spec 的 Book 需求还原（历史归档中的 spec 副本可作为还原参考）。
