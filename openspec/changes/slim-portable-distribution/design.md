# Design

## Context

当前便携分发包在 `sc-main/build.gradle.kts` 的 `javapackager` 扩展中配置为 `bundleJre(true)` + `customizedJre(false)`，即把完整 JDK 运行时镜像打进分发包；且本项目以 JDK 21 打包，JavaPackager 源码仅在 jlink 版本 < 21 时才追加 `--compress=2`，因此当前产物（Windows zip 约 130MB）既不裁剪也不压缩。动机与范围见 `proposal.md`。

## Goals / Non-Goals

**Goals:**
- 分发包自带运行时从全量镜像改为按应用实际使用的 JDK 模块裁剪（`customizedJre = true`）。
- 为裁剪后的 JRE 镜像启用压缩（`--compress=zip-6`），弥补 JDK 21 jlink 不再自动压缩的缺口。
- 显著减小产物体积（Windows zip 目标 50MB 以下），且保持"解压即用、无需预装 Java"行为不变。
- 不改变产物路径、命名与 CI workflow。

**Non-Goals:**
- 不使用 `jrePath` 指定外部 JRE（依赖 jlink 生成，保持可复现）。
- 不裁剪 bootJar 内的应用依赖（`copyDependencies(false)` 维持现状）。
- 不为 macOS 签名/公证、原生安装器做适配。

## Decisions

### D1：启用 `customizedJre(true)`
- 将 `customizedJre(false)` 改为 `customizedJre(true)`（javapackager 默认即 `true`，显式声明便于对照）。
- JavaPackager 将调用 `jdeps --print-module-deps` 分析 bootJar 的模块依赖，再用 `jlink --add-modules <列表>` 生成最小运行时。
- **备选**：`modules(...)` 显式指定完整模块列表、完全跳过 jdeps 自动分析。采用 `additionalModules` 补充而非完全替换，理由：保留 jdeps 自动分析结果作为基线，仅对其缺失的运行时场景做增量补全；若启动验证显示 jdeps 结果严重不可靠（如模块过少），再升级为显式 `modules(...)` 列表。

### D2：用 `additionalModules` 补齐反射/ServiceLoader 所需模块
- Spring Boot fat jar 的依赖位于嵌套 `BOOT-INF/lib`，jdeps 静态分析无法完整解析反射/ServiceLoader 触发的模块依赖，必须显式补齐。
- 推荐模块列表（覆盖 Web / Tomcat / JPA / Hibernate / H2 运行需要）：
  `java.logging`、`java.naming`、`java.sql`、`java.xml`、`java.management`、`java.prefs`、`java.instrument`、`java.security.jgss`、`java.security.sasl`、`jdk.crypto.ec`、`jdk.unsupported`、`jdk.naming.rmi`、`java.net.http`
- 说明：`jdk.crypto.ec`（TLS/EC 证书）、`jdk.unsupported`（Hibernate 使用 `sun.misc.Unsafe` 等内部 API）、`java.sql`/`java.naming`（JPA/H2/DataSource）、`java.security.jgss`（JDBC 认证可选路径）。最终以本机启动冒烟测试结果为准，可增删。

### D3：`additionalJlinkArgs(listOf("--compress=zip-6"))`
- JDK 21 的 jlink 压缩参数语法为 `--compress=zip-N`（N=0..9）。
- 选 `zip-6` 平衡压缩率与链接耗时；如追求极致体积可调至 `zip-9`（链接更慢）。JavaPackager 源码已默认追加 `--no-header-files`、`--no-man-pages`、`--strip-debug`，无需重复配置。

### D4：不改动 CI workflow
- 产物输出路径（`sc-main/build/`）与命名（`sc-main-<version>-<platform>.zip|tar.gz`）不因上述配置变化，`.github/workflows/packaging.yml` 无需调整。

## Risks / Trade-offs

- [jdeps 对 Spring Boot fat jar 的模块分析可能漏掉反射/ServiceLoader 场景，裁剪运行时缺模块导致启动失败] → 以 `additionalModules` 显式补齐运行所需模块；实施时解压产物实际启动应用做全链路冒烟（Web 接口 + H2 读写），并运行 `./gradlew test` 全量测试兜底。
- [裁剪粒度过激导致未来新增功能缺模块] → 保留 `additionalModules` 集中维护；运行时仍含 JRE 核心（非极端最小化），新增模块补充成本低。
- [打包时间因 jdeps/jlink 分析略有增加] → 属可接受的一次性构建成本，CI 矩阵不受影响。
- [压缩级别越高链接越慢] → 采用 `zip-6` 平衡；`zip-9` 仅作为可选项。

## Migration Plan

1. 修改 `sc-main/build.gradle.kts` 的 `javapackager` 块：`customizedJre(true)`、新增 `additionalModules(...)`、新增 `additionalJlinkArgs(listOf("--compress=zip-6"))`。
2. 本机执行 `./gradlew :sc-main:package`，对比产物体积（记录改造前后 zip/tar.gz 大小）。
3. 解压产物并启动应用冒烟（访问接口、执行数据库读写），确认无 `NoClassDefFoundError` / ServiceLoader 加载失败。
4. 运行 `./gradlew test` 全量测试（110+ 项）。
5. 推送后由 CI 三平台矩阵验证。
6. 回滚策略：将 `customizedJre` 改回 `false` 并移除新增配置即可恢复全量运行时。

## Open Questions

无。
