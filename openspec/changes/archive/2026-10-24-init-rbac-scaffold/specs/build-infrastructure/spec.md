# Spec Delta

## Purpose

覆盖工程的构建基础设施：可复现的 Gradle Wrapper 引导、国内镜像依赖解析、Java 21 编译打包链路，以及空壳应用的启动与 HTTP 响应能力。

## ADDED Requirements

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

### Requirement: 空壳应用启动与 HTTP 响应
工程 SHALL 提供可直接启动的空壳应用，启动后 SHALL 监听端口并对 HTTP 请求返回成功响应。

#### Scenario: 应用正常启动
- **WHEN** 开发者启动应用
- **THEN** 应用成功绑定配置端口并无异常退出

#### Scenario: HTTP 请求得到响应
- **WHEN** 客户端向应用发送 HTTP 请求
- **THEN** 应用返回成功状态码的响应
