# Design

## Context

项目为 Gradle + Spring Boot 多上下文教学项目，两个限界上下文（`librarylending`、`studentcourse`）共约 60 个 Java 类，分层为 `web`/`application`/`domain`/`infrastructure`。现状：无任何开源许可声明；仅领域层部分类有手工中文 Javadoc，其余大多无注释。变更动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 全量 `.java`（main + test）获得一致的 MIT license 头与结构化类 Javadoc。
- license 头由 Gradle 插件机械生成并可持续校验；Javadoc 由多个 subagent 并行补充，风格统一。
- 全程不改任何运行行为。

**Non-Goals:**

- 不补方法级 Javadoc（仅类级；方法注释作为可选加分项，不强制）。
- 不引入 `@author`/`@version`/`@since` 标签（git 溯源，简单优先）。
- 不修改现有注释的内容与语义，仅在缺失处新增。

## Decisions

### D1：文件头用 SPDX 短标识，完整 MIT 文本只放根目录 LICENSE

源文件头仅三行（`SPDX-License-Identifier: MIT` + 版权行），完整许可全文放 `LICENSE`。理由：MIT 对源码文件的许可声明用 SPDX 短标识是主流实践，避免每个文件塞 20 行许可全文。

备选：每个文件带完整 MIT 文本（更"正统"但冗长），已被用户否决。

### D2：license 插件用 `skipExistingHeaders = true` 并先跑 format 再补注释

- `skipExistingHeaders = true`：已有 header（含历史手工版权注记）的文件不会被重复插入。
- 执行顺序固定为：`licenseFormatMain`/`licenseFormatTest`（机械加头）→ 再启动注释 subagent。两者会改动同一批 `.java` 文件，**必须串行**避免并发写同一文件。

### D3：注释按分层模板，由多个 subagent 按目录分工

每个 subagent 负责一个目录树，互不重叠，并行安全：

```
subagent A：src/main/.../domain/          （model+service+repository+acl+event+exception，最重）
subagent B：src/main/.../application/ + web/
subagent C：src/main/.../infrastructure/
subagent D：src/test/...                   （全部测试类）
```

分层模板差异：
- domain 对象：一句话职责 + 业务不变量/状态流转（如成绩 `[0,100]`、同一副本至多一条在借）。
- web 类：一句话职责 + 对外契约（对应端点、请求/响应语义）。
- infrastructure 类：一句话职责 + 实现方式（实现哪个端口、用什么机制）。
- 测试类：被测行为与关键断言，而非"职责"。

### D4：license 任务不接入 `build` 门禁，仅保留可运行任务

`licenseMain`/`licenseTest` 检查任务保留但不挂进 `check`/`build` 依赖链——教学项目避免任何一次漏跑 header 就构建失败；需要时手动 `./gradlew licenseMain`。

## Risks / Trade-offs

- [licenseFormat 误判已有注释为 header] → `skipExistingHeaders` 仅按 header 模板匹配，类 Javadoc 在 package 之后不会被误伤；以 `git diff` 抽查 2-3 个文件确认 header 插入位置正确。
- [subagent 并行改不同目录但共享 build.gradle.kts 依赖] → build.gradle.kts 只在 license 步骤改，注释 subagent 完全不碰构建文件。
- [注释与 license header 排版冲突（header 在 package 前、Javadoc 在类前，互不干扰）] → 以 `./gradlew build`（编译通过即语法有效）与 `git diff` 复核为准。
- [测试代码量大导致单个 test subagent 超载] → 若测试文件数 >25，将 subagent D 再按上下文拆为两个。

## Migration Plan

纯源文件注释与构建配置变更，无部署、无数据迁移。回滚为 `git revert`。

## Open Questions

（无）
