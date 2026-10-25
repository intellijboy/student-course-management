# Tasks

## 1. Gradle 便携分发包能力

- [x] 1.1 在 `build.gradle.kts` 引入 JavaPackager 插件（`io.github.javapackager` 2.0.0）并配置 `javapackager` 扩展（`mainClass` 为 Spring Boot 主类、`bundleJre = true`、`customizedJre = false`）——验证：本机执行 `gradle package` 成功，且既有 `bootJar` 构建不受影响
- [x] 1.2 spike 验证插件对 Spring Boot 胖 jar 的打包方式（关闭依赖复制或显式指向 bootJar 产物）——验证：分发包内仅含一份 bootJar，无重复依赖 jar
- [x] 1.3 验证便携分发包结构完整（应用 jar、启动命令、`jre/` 运行时目录）——验证：分发目录含 `jre/` 与启动命令，体积符合全量运行时预期（约 150–300MB）
- [x] 1.4 验证启动命令使用分发包自带运行时启动应用——验证：执行启动命令后应用绑定端口，HTTP 请求返回成功状态码
- [x] 1.5 补充便携分发包使用说明（打包命令、产物位置、目标机运行方式）——验证：按文档命令可复现打包并运行

## 2. 平台压缩包产物

- [x] 2.1 确认并启用压缩包产出：Windows 为 zip，Linux/macOS 为 tar.gz（使用 JavaPackager 内置 zipball/tarball）——验证：本机构建产出对应格式压缩包
- [x] 2.2 验证压缩包解压后可运行——验证：解压到干净目录后执行启动命令，应用正常启动并响应 HTTP

## 3. CI 多平台构建矩阵

- [x] 3.1 新增 `.github/workflows/packaging.yml`：三平台 job（`ubuntu-latest` / `windows-latest` / `macos-latest`），`actions/setup-java` 配置 JDK 21，各执行 `gradle package`——验证：workflow 语法校验通过，三个 job 均成功
- [x] 3.2 配置产物命名 `<app>-<os>-<arch>.zip|tar.gz` 并上传构建产物（`actions/upload-artifact`）——验证：CI 运行后 artifact 列表含三平台压缩包
- [x] 3.3 触发 CI 并确认各平台 runner 产出对应格式压缩包——验证：CI 产物文件名与格式符合预期（Windows→zip，Linux/macOS→tar.gz）

## 4. 集成验证

- [x] 4.1 整体验证三平台 CI 产物一致性——验证：从 CI 下载的 Windows/Linux/macOS 压缩包各解压后均能启动应用并响应 HTTP，分发包体积符合预期
  > 注：CI 已产出三平台压缩包（linux-x64 106MB / windows-x64 104MB / mac-aarch64 102MB，见任务 3.2/3.3）；Windows 产物已在本机完成解压运行与 HTTP 验证（任务 2.2）。从 CI 下载三平台产物逐一解压运行的验证按用户指示跳过，以 CI 全绿 + 本地验证结果作为 4.1 完成依据。
