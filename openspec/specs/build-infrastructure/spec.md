# build-infrastructure Specification

## Purpose

覆盖工程的构建基础设施：可复现的 Gradle Wrapper 引导、国内镜像依赖解析、Java 21 编译打包链路，以及应用的可启动验证能力。

## Requirements

### Requirement: Gradle Wrapper 引导构建
工程 SHALL 通过 Gradle Wrapper 引导构建。Wrapper SHALL 下载并在项目生命周期内固定使用 `gradle-wrapper.properties` 中锁定的 Gradle 发行版。

#### Scenario: 无本地 Gradle 环境首次构建
- **WHEN** 开发者在未安装 Gradle 的机器上首次执行 `gradlew`（Windows 下为 `gradlew.bat`）
- **THEN** Wrapper 从配置的国内镜像地址下载对应发行版并完成构建

#### Scenario: 固定版本一致复现
- **WHEN** 两次构建使用同一 `gradle-wrapper.properties` 配置
- **THEN** 两次构建使用完全相同的 Gradle 版本

### Requirement: 国内镜像依赖解析
构建脚本插件与运行时依赖 SHALL 优先从国内镜像仓库解析，在镜像缺失时回退到官方仓库。

#### Scenario: 插件从镜像解析
- **WHEN** 构建解析一个 Gradle 插件
- **THEN** 系统先尝试国内 `gradle-plugin` 镜像，镜像缺失时回退到官方插件门户

#### Scenario: 运行时依赖从镜像解析
- **WHEN** 构建解析一个运行时依赖（如来自 Maven Central）
- **THEN** 系统先尝试国内 `public` 镜像，镜像缺失时回退到 Maven Central

### Requirement: Java 21 编译与可执行打包
工程 SHALL 以 Java 21 语言级别编译源码，并 SHALL 产出包含全部运行时依赖的可执行 Spring Boot 应用包。

#### Scenario: 编译语言级别
- **WHEN** 执行编译任务
- **THEN** 源码按 Java 21 语言级别编译

#### Scenario: 生成可执行应用包
- **WHEN** 执行打包任务
- **THEN** 产出可独立运行的应用包，其中包含应用自身与全部运行时依赖

### Requirement: 应用可启动
工程 SHALL 提供可直接启动的 Spring Boot 应用，启动后 SHALL 成功绑定配置端口且应用上下文加载无异常。

#### Scenario: 应用正常启动
- **WHEN** 开发者启动应用
- **THEN** 应用成功绑定配置端口并无异常退出

### Requirement: 便携分发包产物
工程 SHALL 产出自带 Java 运行时与启动命令的便携分发包，使目标机器无需预装 Java 即可直接运行应用。

#### Scenario: 生成便携分发包
- **WHEN** 执行便携分发包打包任务
- **THEN** 产出包含应用 jar、自带运行时目录与启动命令的分发目录

#### Scenario: 无 Java 环境直接运行
- **WHEN** 在未安装 Java 的目标机器上执行分发包内的启动命令
- **THEN** 应用使用分发包自带的运行时正常启动

### Requirement: 平台对应压缩包
工程 SHALL 将便携分发包压缩为平台对应格式：Windows 产出 zip，Linux 与 macOS 产出 tar.gz。

#### Scenario: Windows 产物为 zip
- **WHEN** 在 Windows 上完成便携分发包打包
- **THEN** 产出 zip 压缩包，解压后即可运行

#### Scenario: Linux 与 macOS 产物为 tar.gz
- **WHEN** 在 Linux 或 macOS 上完成便携分发包打包
- **THEN** 产出 tar.gz 压缩包，解压后即可运行

### Requirement: 多平台 CI 构建矩阵
工程 SHALL 通过多平台 CI 矩阵在 Windows、Linux、macOS 上分别构建各自的便携分发包压缩包，并将压缩包作为构建产物供下载。

#### Scenario: 三平台各自产出分发包
- **WHEN** CI 分别在 Windows、Linux、macOS 的 runner 上执行打包任务
- **THEN** 各 runner 产出对应平台的便携分发包压缩包

#### Scenario: 构建产物可下载
- **WHEN** 开发者进入 CI 构建结果页
- **THEN** 可下载各平台的分发包压缩包
