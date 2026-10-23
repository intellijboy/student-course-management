# build-infrastructure Specification

## Purpose

覆盖工程的构建基础设施：可复现的 Gradle Wrapper 引导、国内镜像依赖解析、Java 21 编译打包链路，以及空壳应用的启动与 HTTP 响应能力。

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

### Requirement: 空壳应用启动与 HTTP 响应
工程 SHALL 提供可直接启动的空壳应用，启动后 SHALL 监听端口并对 HTTP 请求返回成功响应。

#### Scenario: 应用正常启动
- **WHEN** 开发者启动应用
- **THEN** 应用成功绑定配置端口并无异常退出

#### Scenario: HTTP 请求得到响应
- **WHEN** 客户端向应用发送 HTTP 请求
- **THEN** 应用返回成功状态码的响应

### Requirement: Book 列表查询
系统 SHALL 提供端点返回 Book 资源列表，每个条目包含 id、标题、作者。

#### Scenario: 返回书目列表
- **WHEN** 客户端请求 Book 列表端点
- **THEN** 系统返回 200 及全部 Book 条目

### Requirement: Book 单个查询
系统 SHALL 提供端点按 id 返回单个 Book；id 不存在时 SHALL 返回 404。

#### Scenario: 查询存在的 Book
- **WHEN** 客户端请求某个已存在 id 的 Book
- **THEN** 系统返回 200 及该 Book 的完整信息

#### Scenario: 查询不存在的 Book
- **WHEN** 客户端请求一个不存在的 id
- **THEN** 系统返回 404

### Requirement: Book 创建
系统 SHALL 提供端点创建 Book，创建成功 SHALL 返回 201 及新创建资源的 id。

#### Scenario: 创建 Book
- **WHEN** 客户端提交包含标题与作者的 Book 数据
- **THEN** 系统创建该书目并返回 201 与新资源的 id

### Requirement: Book 更新
系统 SHALL 提供端点按 id 更新 Book 的标题与作者；id 不存在时 SHALL 返回 404。

#### Scenario: 更新存在的 Book
- **WHEN** 客户端提交对已存在 Book 的标题或作者修改
- **THEN** 系统更新该书目并返回 200 及更新后的信息

#### Scenario: 更新不存在的 Book
- **WHEN** 客户端请求更新一个不存在的 id
- **THEN** 系统返回 404

### Requirement: Book 删除
系统 SHALL 提供端点按 id 删除 Book；删除成功 SHALL 返回 204，id 不存在时 SHALL 返回 404。

#### Scenario: 删除存在的 Book
- **WHEN** 客户端请求删除一个已存在的 id
- **THEN** 系统删除该书目并返回 204

#### Scenario: 删除不存在的 Book
- **WHEN** 客户端请求删除一个不存在的 id
- **THEN** 系统返回 404
