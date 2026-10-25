# Proposal

## Why

当前 `build-infrastructure` 已能产出 Spring Boot 可执行 fat jar（bootJar），但该包要求目标机器预装 Java 21，无法直接拷贝分发。工程需要一个自带 Java 运行时与启动命令的便携分发包，并配合多平台 CI 构建矩阵，实现"解压即用、跨平台分发"。

## What Changes

- 引入 JavaPackager Gradle 插件（`io.github.javapackager` 2.x）并配置便携分发包产物：内置运行时（`jre/`）、启动命令（Windows 下为应用 `.exe`，Linux/macOS 下为启动脚本）、可执行 jar 与依赖。
- 分发包以压缩包形式产出：Windows 产出 zip，Linux/macOS 产出 tar.gz，便于直接拷贝分发到目标平台。
- 运行时采用**全量运行时**策略（`customizedJre = false`），避免 jdeps 裁剪运行时导致 Spring Boot/JPA 反射加载在目标机缺模块。
- 新增 GitHub Actions 多平台构建矩阵（ubuntu-latest / windows-latest / macos-latest），自动产出三平台便携压缩包作为构建产物。
- **非目标**：原生安装器（exe/msi/deb/rpm/dmg）、macOS 签名与公证（接受"右键→打开"的 Gatekeeper 摩擦）、H2 数据库持久化改造（维持内存库）。

## Capabilities

### New Capabilities

<!-- 无新增 capability：便携分发包是构建基础设施的延续行为，归入既有 build-infrastructure capability。 -->

### Modified Capabilities

- `build-infrastructure`: 新增构建产物级需求——工程 SHALL 产出自带 Java 运行时与启动命令的便携分发包（zip/tar 压缩包），并 SHALL 通过多平台 CI 矩阵产出 Windows/Linux/macOS 三平台的对应分发包。

## Impact

- **构建脚本**：`build.gradle.kts` 新增 JavaPackager 插件声明与 `javapackager` 扩展配置（`mainClass`、`bundleJre`、`customizedJre`）；插件从既有阿里云 `gradle-plugin` 镜像与 Gradle Plugin Portal 回退解析，无需改动 `settings.gradle.kts` 仓库配置。
- **CI**：新增 `.github/workflows/` 多平台打包矩阵（当前仓库无 CI 配置）。
- **构建产物**：新增便携分发包目录（app 目录 + `jre/`）与三平台压缩包；分发包体积因全量运行时约为 150–300MB。
- **应用行为**：REST API、领域规则、页面功能均无变化；应用入口与运行行为保持不变。
