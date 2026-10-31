# Proposal

## Why

项目目前没有任何开源许可声明，且大量类缺少文档注释（此前仅领域层手工补过部分 Javadoc）。本次统一为所有源代码添加 MIT License 头，并为每个类补充结构化文档注释，使代码可对外发布、可读性与可维护性对齐教学演示的定位。

## What Changes

- 引入 `com.github.hierynomus.license` 插件（0.16.1），为所有 `src/main` 与 `src/test` 下的 `.java` 源文件添加 MIT License 头。
- 新增根目录 `LICENSE`（完整 MIT 文本，版权行 `Copyright (c) 2026 Intellijboy`）与 `LICENSE_HEADER`（SPDX 短标识版头）。
- 通过 `./gradlew licenseFormatMain licenseFormatTest` 一键补齐缺失的 license 头，`licenseMain`/`licenseTest` 作为一致性检查任务保留。
- 为每个类补充结构化中文 Javadoc：一句话职责 + 按分层定制的说明（领域对象含业务不变量、web 类含对外契约、基础设施类含实现方式、测试类含被测行为），覆盖生产与测试代码。
- **不改变**任何运行行为、API 契约或 spec 需求；纯注释与构建工具变更。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无 —— 本次为注释与构建工具变更，不产生任何 spec 级行为变化，故本变更声明 `skip_specs: true`。）

## Impact

- 受影响文件：全部 `.java` 源文件（`src/main/java` 约 40+ 类、`src/test/java` 约 20+ 类）追加 license 头与类注释；`build.gradle.kts` 增加插件与 `license {}` 配置块。
- 新增文件：根目录 `LICENSE`、`LICENSE_HEADER`。
- 依赖与构建：新增 Gradle 插件依赖（`com.github.hierynomus.license:0.16.1`），不影响运行时依赖与 `./gradlew build` 结果。
