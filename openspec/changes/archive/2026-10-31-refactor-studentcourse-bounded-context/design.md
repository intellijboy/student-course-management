# Design

## Context

现有 `studentcourse` 模块为技术层次优先包结构（`cn.intellijboy.demo.{web,application,domain,infrastructure}.studentcourse`），与 `librarylending` 的业务域优先（`cn.intellijboy.demo.librarylending.{web,application,domain,infrastructure}`）不一致。动机与范围见 proposal.md - Why/What Changes。本设计为纯包结构重组（行为不变），规格无变化（skip_specs）。

## Goals / Non-Goals

**Goals:**

- 将 `studentcourse` 重组为业务域优先：`cn.intellijboy.demo.studentcourse.{web,application,domain,infrastructure}`，与 `librarylending` 对齐。
- 同步更新跨模块引用（`librarylending` 防腐层适配器）与测试包路径。
- 保持行为完全不变：编译、全部测试、REST API 与页面语义不变。

**Non-Goals:**

- 不修改任何领域逻辑、REST API 语义或前端页面。
- 不调整 `librarylending` 模块（除必要 import 更新外）。
- 不改变 `student-course-management` / `library-lending` spec。

## Decisions

### D1. 包结构映射（业务域优先）

将 `studentcourse` 子包整体从 `cn.intellijboy.demo.<layer>.studentcourse` 迁移为 `cn.intellijboy.demo.studentcourse.<layer>`：

| 旧包（技术层次优先） | 新包（业务域优先） |
| --- | --- |
| `cn.intellijboy.demo.web.studentcourse` | `cn.intellijboy.demo.studentcourse.web` |
| `cn.intellijboy.demo.application.studentcourse` | `cn.intellijboy.demo.studentcourse.application` |
| `cn.intellijboy.demo.domain.studentcourse`（含 model/exception/repository/event） | `cn.intellijboy.demo.studentcourse.domain`（子包不变） |
| `cn.intellijboy.demo.infrastructure.studentcourse`（含 repository/persistence/mapper/event） | `cn.intellijboy.demo.studentcourse.infrastructure`（子包不变） |

迁移采用 git 可识别的方式（`git mv` 目录 + 更新 `package`/`import` 声明），保证历史可追溯。

### D2. 跨模块引用同步

`librarylending` 的 `ReaderCatalogGatewayAdapter` 当前 import `cn.intellijboy.demo.infrastructure.studentcourse.repository.JpaStudentRepository`，迁移后更新为 `cn.intellijboy.demo.studentcourse.infrastructure.repository.JpaStudentRepository`。除此之外，`librarylending` 不依赖 `studentcourse` 领域模型，无其他引用需要更新。

### D3. 测试包路径同步

`src/test/java/cn/intellijboy/demo/{web,application,domain,infrastructure}.studentcourse/**` 测试同步迁移至 `cn.intellijboy.demo.studentcourse.{web,application,domain,infrastructure}`，包声明与 import 同步更新。

### D4. 验证方式

- 迁移后运行 `./gradlew test` 全量通过（学生/课程/成绩既有测试回归）。
- `git status` 确认迁移被识别为 rename（保留历史）。
- 核对 `docs/ddd-tutorials/` 中涉及 `studentcourse` 代码路径的描述（08 篇导览中的 `studentcourse/` 目录说明在新结构下依然成立，无需改动；若其他篇引用旧包路径则更新）。

## Risks / Trade-offs

- [大规模包移动导致编译/import 遗漏] → 全量测试回归兜底；迁移按目录分步执行，每步编译验证。
- [跨模块引用遗漏（防腐层 import）] → 显式任务覆盖 `ReaderCatalogGatewayAdapter` 的 import 更新。
- [git 识别迁移为 delete+add 而非 rename] → 使用 `git mv` 并保持内容不变，提高 rename 识别率；即使识别为 delete+add，行为不受影响。
- [与教程文档描述不一致] → 核对并更新 `docs/ddd-tutorials/` 中引用的代码路径描述。

## Migration Plan

- 无数据迁移（纯代码重组）。
- 回滚策略：git revert 该提交即可恢复原包结构；行为不变，风险低。

## Open Questions

- 无。业务域优先方向、包命名（对齐 `librarylending`）与"行为不变"约束已明确。
