# Tasks

## 1. License 基础与插件接入

- [x] 1.1 创建根目录 `LICENSE`（完整 MIT 文本，版权行 `Copyright (c) 2026 Intellijboy`）与 `LICENSE_HEADER`（SPDX 短标识：`SPDX-License-Identifier: MIT` + 版权行）；验证：两文件存在且内容与设计 D1 一致。
- [x] 1.2 在 `build.gradle.kts` 的 plugins 块加入 `id("com.github.hierynomus.license") version "0.16.1"`，并配置 `license { header = rootProject.file("LICENSE_HEADER"); mapping { java = "SLASHSTAR_STYLE" }; skipExistingHeaders = true }`；验证：`./gradlew help` 可正常解析构建，无插件加载错误。
- [x] 1.3 运行 `./gradlew licenseFormatMain licenseFormatTest` 为全部 `.java` 文件补 license 头，再运行 `./gradlew licenseMain licenseTest` 确认一致性检查通过；验证：`licenseMain`/`licenseTest` 通过，且 `git diff` 抽查确认 header 插入在每个文件的 `package` 声明之前。

## 2. 类文档注释补充（多 subagent 并行）

- [x] 2.1 subagent A 为 `src/main/java/cn/intellijboy/demo/*/domain/` 全部类补类 Javadoc（一句话职责 + 关键业务不变量/状态流转，中文）；验证：该目录每个 `.java` 类均新增类级 Javadoc 且 `./gradlew compileJava` 通过。
- [x] 2.2 subagent B 为 `src/main/java/cn/intellijboy/demo/*/application/` 与 `*/web/` 全部类补类 Javadoc（应用服务：用例编排；web 类：职责 + 对应端点/对外契约）；验证：目录内每个类均有 Javadoc 且编译通过。
- [x] 2.3 subagent C 为 `src/main/java/cn/intellijboy/demo/*/infrastructure/` 全部类补类 Javadoc（职责 + 实现的端口/机制）；验证：目录内每个类均有 Javadoc 且编译通过。
- [x] 2.4 subagent D 为 `src/test/java/cn/intellijboy/demo/` 全部测试类补类 Javadoc（被测行为与关键断言，非职责描述）；验证：每个测试类均有 Javadoc 且 `./gradlew test` 通过。

## 3. 全量回归与复核

- [x] 3.1 执行 `./gradlew build`；验证：BUILD SUCCESSFUL，全部测试通过（注释变更无任何语法/语义副作用）。
- [x] 3.2 复核变更面：`git diff --stat` 应仅含 `.java` 注释、`LICENSE`/`LICENSE_HEADER`、`build.gradle.kts`，无任何运行行为改动；验证：diff 内容复核无超出注释与 license 的变更，`licenseMain licenseTest` 复跑仍通过。
