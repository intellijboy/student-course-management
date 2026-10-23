# Design

## Context

- 见 `proposal.md - Why`。这是一个全新空壳工程，当前仓库仅有 OpenSpec 骨架（`openspec/`）与 opencode 配置（`.opencode/`），无任何构建产物。
- 本机环境：Java 21 (GraalVM)，未安装 Gradle。
- 构建脚本语言采用 Kotlin DSL；业务源码使用 Java；包路径 `cn.intellijboy.demo`；单模块。

## Goals / Non-Goals

**Goals:**
- 落地可复现的 Gradle Wrapper 引导（本机无 Gradle 也能开箱构建）。
- 三层下载路径全部走国内镜像：Wrapper 发行版、构建插件、运行时依赖。
- 产出可启动、可打包、可被 HTTP 访问的空壳应用。
- 所有配置固化在工程内（`gradle-wrapper.properties`、`settings.gradle.kts`），不依赖开发者本机环境变量或全局 `init.gradle`。

**Non-Goals:**
- RBAC 业务功能（后续单独变更规划）。
- 多模块拆分、CI/CD、容器化、配置中心。
- 引入 `spring-boot-starter-actuator` 等扩展 starter（本次仅验证 web 空壳）。

## Decisions

### D1. Wrapper 引导方式
本机无 Gradle，需要"先有鸡"问题。方案：从腾讯云镜像下载 `gradle-9.8.2-bin.zip` 到临时目录，解压后用其 `bin/gradle` 执行 `wrapper` 任务，生成 `gradlew`/`gradlew.bat`/`gradle/wrapper/*`；随后将临时发行版删除。

- **备选**：手写 wrapper 脚本与 properties 并单独下载 wrapper jar —— 易出错，且 wrapper jar 校验繁琐，不采用。
- **备选**：安装全局 Gradle —— 违背"工程内固化、不依赖本机"的目标，不采用。

### D2. 镜像三层配置（核心）
```
wrapper 发行版    gradle-wrapper.properties  distributionUrl
                 -> https://mirrors.cloud.tencent.com/gradle/gradle-9.8.2-bin.zip

构建插件解析      settings.gradle.kts  pluginManagement.repositories
                 -> maven.aliyun.com/repository/gradle-plugin
                 -> maven.aliyun.com/repository/public
                 -> gradlePluginPortal()           （兜底）

运行时依赖解析    settings.gradle.kts  dependencyResolutionManagement.repositories
                 -> maven.aliyun.com/repository/public
                 -> mavenCentral()                 （兜底）
```
- 镜像置于官方仓库之前，满足"优先国内镜像"的诉求。
- 保留官方仓库兜底，应对镜像收录缺失或元数据不完整（Gradle 依赖解析为"元数据优先"，命中镜像元数据即固定来源仓库，故镜像完整性是首要考虑）。
- **备选**：仅配置镜像、不配官方兜底 —— 单点依赖镜像完整性，构建失败时无回退，不采用。

### D3. 构建脚本（build.gradle.kts）
- 插件：`org.springframework.boot` + `io.spring.dependency-management`，版本取 Spring Boot 4.1.x 最新稳定 patch（写入固定版本号，不用动态版本）。
- `java { toolchain }` 不启用强制下载；直接声明 `java { sourceCompatibility/targetCompatibility }` 或 `options.release` 为 21——本机 JDK 21 即目标版本，无需 toolchain 自动下载 JDK。
- 依赖：`spring-boot-starter-web`（implementation）、`spring-boot-starter-test`（testImplementation）。

### D4. 空壳应用结构
```
src/main/java/cn/intellijboy/demo/
  |-- RbacOpenspecDemoApplication.java   启动类
  `-- web/DemoController.java            GET /demo 返回 200
src/test/java/cn/intellijboy/demo/
  `-- ...ApplicationTests.java           上下文加载测试 + MVC 冒烟测试
```
- 提供 `GET /demo` 端点验证 HTTP 链路（空壳验证，非业务）。

### D5. .gitignore 与版本控制
- 现有 `.gitignore` 已含 `.gradle/`、`build/`、`out/`、`bin/`、`*.class`，无需大改。
- Wrapper 相关文件（`gradlew`、`gradlew.bat`、`gradle/wrapper/*`）与构建脚本 **必须提交**，保证开箱构建。

## Risks / Trade-offs

- [Gradle 依赖"元数据优先"：镜像含元数据但缺构件时，构建不从兜底仓库重试] → 优先选用收录完整、同步及时的阿里云 `public`/`gradle-plugin` 镜像；若实际遇到缺构件，针对性加 `content { }` 过滤或调整仓库顺序，作为 apply 阶段的排障项。
- [Spring Boot 4.1.x 较新，阿里云镜像同步可能短暂滞后] → 官方仓库兜底兜住；实现时确认已发布的稳定 patch 版本号。
- [腾讯云 gradle 发行版镜像同步滞后] → 已核实 `gradle-9.8.2-bin.zip` 于发布当日同步；若再次遇到版本缺失，回退到较近的 9.x 或 8.14.x 维护线版本（镜像均已收录）。

## Migration Plan

绿地项目，无既有系统可迁移、无需回滚路径。

## Open Questions

- Spring Boot 4.1.x 的具体 patch 版本号：apply 阶段以当时最新稳定版为准写入 `build.gradle.kts`。不影响 spec、方案与任务拆解。
