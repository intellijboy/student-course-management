# Design

## Context

当前工程为 Spring Boot 4.1.2 + Java 21 的**非模块化**应用（无 `module-info.java`），`build-infrastructure` 已覆盖 bootJar 可执行包，但产物要求目标机预装 Java 21。构建使用 Gradle 9.8.2（腾讯镜像），插件经阿里云 `gradle-plugin` 镜像 + 官方插件门户回退解析。仓库当前无任何 CI 配置。动机见 proposal.md - Why。

## Goals / Non-Goals

**Goals:**
- 产出自带 Java 运行时与启动命令的便携分发包（zip / tar.gz）
- Windows / Linux / macOS 三平台各自产出对应压缩包
- 通过 CI 矩阵自动化打包并将压缩包作为构建产物上传

**Non-Goals:**
- 原生安装器（exe / msi / deb / rpm / dmg）
- macOS 签名与公证
- H2 数据库持久化改造
- 额外架构（本期仅 Windows/Linux x64 + macOS arm64；macOS x64 与 Linux arm64 不在本期）

## Decisions

### 决策 1：选用 JavaPackager（`io.github.javapackager` 2.0.0）

理由：
- Gradle 侧 `bundleJre` 默认开启，开箱即得"自带 JRE + 启动命令 + 压缩包"；
- 仅需配置 `mainClass`，官方明确支持 Gradle 9（与工程 Gradle 9.8.2 兼容）；
- 输出"原生 app 目录 + zipball/tarball"，与"便携目录 + 压缩包"需求精确吻合。

备选与取舍：
- **Badass Runtime**（`org.beryx.runtime` 2.0.1，ASL）：非模块化专用、协议宽松，但模块列表需手工维护（jdeps 探测 + 逐模块补齐），维护节奏偏缓；
- **Badass JLink**（`org.beryx.jlink`）：模块化专用，本项目非模块化，排除；
- **jpackage 原生**（或 `org.panteleyev.jpackageplugin`）：零外部插件、无 GPL，但需自写压缩打包与 CI 接线。

取舍：JavaPackager 以开发体验换取 GPL-3.0 构建期许可。若团队合规评审不接受 GPL，回退到 jpackage 原生方案（决策 1 的备选），spec 层不受影响。

### 决策 2：全量运行时（`customizedJre = false`）

理由：Spring Boot + JPA/Hibernate 重度使用反射与 ServiceLoader，jdeps 裁剪的最小运行时极易在目标机运行期缺模块、难排查。先全量保证稳定。

取舍：裁剪可显著减小分发包体积（150–300MB 降至几十 MB），但需要逐模块补齐的反复验证，列为后续优化项而非本期目标。

### 决策 3：以 bootJar 作为分发包内的唯一 jar

JavaPackager 默认按"应用 jar + 运行时依赖"拆开打包；Spring Boot 的 bootJar 是自包含胖 jar，分发包内应只含 bootJar，避免依赖重复与体积膨胀。落地第一步用 spike 验证插件对胖 jar 的处理方式（是否需关闭依赖复制 / 显式指向 bootJar 产物）。

**Spike 结论（已实现落地）：**
- `runnableJar` 显式指向 `bootJar` 产物，`copyDependencies = false`，分发包内仅含一份 bootJar，无重复依赖 jar。
- 因 `copyDependencies = false` 时插件不会自动生成类路径，需显式配置 `classpath("<name>-<version>.jar")` 指向分发包内的 bootJar。
- **启动入口采用 `org.springframework.boot.loader.launch.JarLauncher`**：Spring Boot fat jar 为嵌套结构（`BOOT-INF/`），JavaPackager 生成的启动命令（launch4j exe、Linux/macOS 启动脚本）均以 `java -cp <jar> <mainClass>` 方式启动，无法直接加载 fat jar 内的应用主类；JarLauncher 从 MANIFEST 的 Start-Class 加载应用，行为与 `java -jar` 一致。
- `winConfig.wrapJar` 与 `linuxConfig.wrapJar` 设为 `false`（默认 `true` 会把 jar 嵌入 exe / 拼接进脚本），保证分发包内保留独立 bootJar 文件，结构为"应用 jar + 启动命令 + `jre/`"。
- 平台压缩包命名 `<name>-<version>-<platform>.zip|tar.gz`（Gradle 插件忽略 `zipballName`/`tarballName`），CI 上传前按 `<app>-<os>-<arch>.<ext>` 重命名。

### 决策 4：GitHub Actions 三平台矩阵

- 三个并行 job：`ubuntu-latest` / `windows-latest` / `macos-latest`，各执行 `gradle package`；
- 产物命名 `<app>-<os>-<arch>.zip|tar.gz`，通过 `actions/upload-artifact` 上传；
- runner 架构：macos-latest 现为 arm64，Windows/Linux 为 x64；
- JDK 21 由 runner 上的 `actions/setup-java` 提供（分发包构建所需）。

### 决策 5：分发包命名与布局

- 应用名沿用 rename change 的最终应用名 `student-course-management`；若 rename 未先行落地，本期以当前 `rootProject.name` 为准，并将该约定记录为 apply 期假设；
- 布局：分发目录 = 应用 jar + 启动命令 + `jre/` 运行时目录。

## Risks / Trade-offs

- [全量运行时使分发包体积达 150–300MB] → 明确为可接受代价；裁剪作为后续优化项，不动摇本期方案
- [JavaPackager 对 Spring Boot 胖 jar 的打包方式需对齐] → 已完成 spike 并落地：`copyDependencies=false` + `runnableJar` 指向 bootJar + 显式 `classpath` + 启动入口用 `JarLauncher` + 平台 `wrapJar=false`（见决策 3）
- [GPL-3.0 构建期插件许可] → 若合规评审不通过，回退 jpackage 原生方案（决策 1 备选）
- [macOS 未签名 app 被 Gatekeeper 拦截] → 接受"右键→打开"摩擦，签名公证列为非目标
- [与 rename change 的顺序耦合] → CI 产物命名依赖最终应用名；打包 change 应排在其后，或按决策 5 显式约定命名

## Migration Plan

本期为新增能力：新增插件声明与 CI 工作流，不改变既有 bootJar 构建。回滚方式：移除插件声明与 `.github/workflows` 文件即可，不影响现有打包链路。

## Open Questions

- **分发包应用名取值**：取决于 rename change 是否先行落地。属命名约定，按决策 5 处理，不影响 spec 与整体方案。
- **macOS x64 是否纳入**：本期默认不做（macos-latest 为 arm64），后续如需再加 job。
