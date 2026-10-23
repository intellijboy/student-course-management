# Proposal

## Why

这是一个全新的 Spring Boot 工程（rbac-openspec-demo），需要一个可构建、可运行、可验证的空壳脚手架作为后续业务开发的起点。当前仓库只有 OpenSpec 骨架，没有任何工程构建产物。

## What Changes

- 初始化一个基于 **Gradle Wrapper** 的单模块工程，构建脚本语言采用 **Kotlin DSL**（`build.gradle.kts`）。
- 技术栈锁定：**Java 21**（编译目标与本机 JDK 21 一致）、**Spring Boot 4.1.x**、**Gradle 9.8.2**。
- 引入 `spring-boot-starter-web`，提供一个可响应 HTTP 的空壳应用（REST 入口 + 启动类）。
- 配置国内镜像策略，覆盖三层下载路径：
  - Wrapper 发行版分发地址 → 腾讯云 Gradle 镜像。
  - 构建脚本插件解析 → 阿里云 `gradle-plugin` 镜像，官方插件门户兜底。
  - 运行时依赖解析 → 阿里云 `public` 镜像，Maven Central 兜底。
- 采用单模块结构，RBAC 业务不在本次范围。

## Capabilities

### New Capabilities
- `build-infrastructure`: 覆盖工程的构建基础设施——Gradle Wrapper 引导、镜像下载策略、编译/打包/运行链路、空壳应用的启动与健康检查。

### Modified Capabilities
<!-- 无既有 capability 变更 -->

## Impact

- **代码**：新增 `settings.gradle.kts`、`build.gradle.kts`、`gradle/wrapper/*`、`gradlew`/`gradlew.bat`、`src/main/java` 与 `src/test/java` 目录结构。
- **配置**：新增镜像仓库配置（`settings.gradle.kts` 中 `pluginManagement` 与 `dependencyResolutionManagement`）。
- **依赖**：引入 Spring Boot Gradle 插件、Spring Boot Starter Web、JUnit 与 Spring Boot Test。
- **系统**：仓库现有 `openspec/` 与 `.opencode/` 内容保持不变；`.gitignore` 按需补充 Gradle 产物忽略项。
