# Proposal

## Why

当前便携分发包采用全量运行时策略（`customizedJre = false`），并且 JDK 21 的 jlink 已不再自动启用镜像压缩，导致产物 zip 体积高达约 130MB。分发包体积过大，影响下载与分发效率，需要裁剪自带运行时并启用镜像压缩以显著瘦身。

## What Changes

- 启用运行时模块裁剪（`customizedJre = true`）：javapackager 通过 jdeps 分析 bootJar 的模块依赖，用 jlink 仅打包应用实际使用的 JDK 模块，取代当前打包整个运行时镜像的策略。
- 通过 `additionalModules` 显式补齐 Spring Boot Web / JPA / H2 / Tomcat 在反射与 ServiceLoader 场景下所需的 JDK 模块（如 `java.sql`、`java.naming`、`java.management`、`jdk.crypto.ec`、`jdk.unsupported` 等），规避 Spring Boot fat jar 嵌套结构导致 jdeps 静态分析漏掉运行时模块的问题。
- 通过 `additionalJlinkArgs` 增加 `--compress=zip-6`，为 JRE 镜像启用压缩（JDK 21 的 jlink 压缩参数语法），弥补 JDK 21+ 不再自动压缩的缺口。
- 保留既有策略：`bundleJre(true)`（自带运行时）、`copyDependencies(false)`（分发包仅含 bootJar，无重复依赖）、`generateInstaller(false)`（不产安装器）。
- 体积目标：Windows zip 产物从约 130MB 降至 50MB 以下（Linux/macOS tar.gz 同比缩小）。
- **非目标**：原生安装器（exe/msi/deb/rpm/dmg）、macOS 签名与公证、H2 数据库持久化改造、CI workflow 与产物路径/命名变更。

## Capabilities

### New Capabilities

<!-- 无新增 capability：运行时裁剪是便携分发包既有行为的延续，归入 build-infrastructure。 -->

### Modified Capabilities

- `build-infrastructure`: 修改「便携分发包产物」需求——分发包自带运行时由全量运行时改为按应用实际使用的 JDK 模块裁剪的最小运行时，且产物压缩体积 SHALL 显著小于全量运行时产物。

## Impact

- **构建脚本**：`sc-main/build.gradle.kts` 的 `javapackager` 扩展调整 `customizedJre`，新增 `additionalModules` 与 `additionalJlinkArgs` 配置。
- **CI**：无改动，产物路径与命名保持不变，`.github/workflows/packaging.yml` 无需调整。
- **构建产物**：分发包体积显著减小（预计 zip 130MB → 50MB 以下）；打包时间因 jdeps/jlink 分析略有增加。
- **应用行为**：REST API、领域规则、页面功能均无变化；需验证裁剪运行时后反射/ServiceLoader 场景无缺模块，应用可正常启动。
