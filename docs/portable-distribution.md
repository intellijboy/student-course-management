# 便携分发包使用说明

本工程通过 [JavaPackager](https://github.com/javapackager/JavaPackager)（Gradle 插件 `io.github.javapackager` 2.0.0）产出**自带 Java 运行时与启动命令的便携分发包**：目标机器无需预装 Java，解压即可运行。

## 打包命令

在工程根目录执行（Windows 使用 `gradlew.bat`，Linux/macOS 使用 `./gradlew`）：

```bash
# Windows
gradlew.bat clean package

# Linux / macOS
./gradlew clean package
```

`package` 任务在 `build` 之后执行，因此会先完成编译、测试与 `bootJar`，再生成便携分发包，不影响既有构建链路。

> 说明：JavaPackager 为 GPL-3.0 构建期许可插件，仅影响构建过程，不影响运行产物与运行时。

## 产物位置

构建产物位于 `build/` 目录：

| 产物 | 位置 | 说明 |
| --- | --- | --- |
| 应用目录 | `build/student-course-management/` | 便携分发包：应用 jar + 启动命令 + `jre/` 运行时 |
| Windows 压缩包 | `build/student-course-management-<version>-windows.zip` | 平台对应格式 |
| Linux / macOS 压缩包 | `build/student-course-management-<version>-linux.tar.gz`（Linux）/ `...-mac.tar.gz`（macOS） | 平台对应格式 |

应用目录结构（Windows 示例）：

```
build/student-course-management/
├── student-course-management-0.0.1-SNAPSHOT.jar   # Spring Boot 可执行胖 jar
├── student-course-management.exe                  # Windows 启动命令
└── jre/                                           # 自带全量 Java 21 运行时
```

- 分发包内含**全量运行时**（`customizedJre = false`），体积约 150–300MB，避免 jdeps 裁剪导致 Spring Boot / JPA 反射加载缺模块。
- 分发包内仅含**一份** `bootJar`（Spring Boot 可执行胖 jar），不复制依赖 jar。

## 目标机运行方式

目标机器**无需预装 Java**。

### Windows

1. 将 `student-course-management-<version>-windows.zip` 拷贝到目标机并解压。
2. 进入 `student-course-management/` 目录，双击或命令行执行：

```bash
student-course-management.exe
```

3. 浏览器访问 `http://localhost:8080` 查看学生 / 课程 / 成绩管理页面。

### Linux

1. 解压 `student-course-management-<version>-linux.tar.gz`。
2. 执行启动脚本（脚本已具备可执行权限）：

```bash
./student-course-management
```

3. 浏览器访问 `http://localhost:8080`。

### macOS

1. 解压 `student-course-management-<version>-mac.tar.gz`。
2. 打开应用包：

```bash
open student-course-management.app
```

3. 若首次启动被 Gatekeeper 拦截（应用未签名公证），请在 Finder 中右键应用 →「打开」以放行。
4. 浏览器访问 `http://localhost:8080`。

## 平台压缩包

- Windows 平台构建产出 `.zip`；Linux / macOS 平台构建产出 `.tar.gz`。
- 三平台压缩包由 `.github/workflows/packaging.yml` CI 矩阵（`ubuntu-latest` / `windows-latest` / `macos-latest`）自动构建并上传为构建产物，产物命名 `student-course-management-<os>-<arch>.zip|tar.gz`。

## 注意事项

- **应用数据**：当前后端使用 H2 内存数据库，应用重启后数据清空，属于既有设计（本变更不涉及持久化改造）。
- **macOS 签名公证**：产物未签名公证，接受「右键 → 打开」摩擦，见上。
- **运行时版本**：分发包自带运行时与构建所用 JDK 一致（Java 21）。
