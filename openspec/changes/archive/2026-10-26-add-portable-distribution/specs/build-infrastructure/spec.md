# Spec Delta

## ADDED Requirements

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
