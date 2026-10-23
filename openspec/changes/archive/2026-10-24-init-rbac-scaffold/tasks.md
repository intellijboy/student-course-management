# Tasks

## 1. Gradle Wrapper 引导与镜像下载

- [x] 1.1 从腾讯云镜像下载 `gradle-9.8.2-bin.zip` 至临时目录并解压，验证解压后的 `gradle` 可执行（运行 `gradle --version` 输出 9.8.2）
- [x] 1.2 使用临时 Gradle 执行 `gradle wrapper --gradle-version 9.8.2` 生成 wrapper 文件，验证 `gradlew`、`gradlew.bat`、`gradle/wrapper/*` 生成，且 `gradle-wrapper.properties` 的 `distributionUrl` 指向腾讯云镜像
- [x] 1.3 运行 `./gradlew --version`，验证 wrapper 首次从腾讯云镜像成功拉取发行版并输出 9.8.2

## 2. 构建脚本与国内镜像仓库配置

- [x] 2.1 编写 `settings.gradle.kts`：`rootProject.name = "rbac-openspec-demo"`，`pluginManagement.repositories` 配置阿里云 `gradle-plugin`、`public` 镜像并以 `gradlePluginPortal()` 兜底；`dependencyResolutionManagement.repositories` 配置阿里云 `public` 镜像并以 `mavenCentral()` 兜底；验证 `./gradlew help` 无仓库解析错误
- [x] 2.2 编写 `build.gradle.kts`：声明 Spring Boot 插件与依赖管理插件（版本为当时最新稳定 4.1.x）、Java 21 编译级别、`spring-boot-starter-web` 与 `spring-boot-starter-test` 依赖；验证 `./gradlew compileJava` 成功且依赖均从阿里云镜像解析
- [x] 2.3 核对 `.gitignore`：确认 `.gradle/`、`build/`、`out/`、`bin/`、`*.class` 已被忽略，wrapper 相关文件不在忽略范围

## 3. 空壳应用实现

- [x] 3.1 创建启动类 `cn.intellijboy.demo.RbacOpenspecDemoApplication`（含 `@SpringBootApplication`），验证 `./gradlew compileJava` 通过
- [x] 3.2 创建 `cn.intellijboy.demo.web.DemoController`，提供 `GET /demo` 返回成功响应，验证编译通过
- [x] 3.3 编写测试：应用上下文加载测试与 `GET /demo` 的 MockMvc 冒烟测试，运行 `./gradlew test` 验证全部通过

## 4. 打包与运行链路验证

- [x] 4.1 运行 `./gradlew build`，验证编译、测试、打包全链路通过并产出可执行应用包
- [x] 4.2 启动应用（`./gradlew bootRun` 或执行打出的 jar），请求 `GET /demo` 验证返回成功响应，随后停止应用

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更。
- 归档后运行 openspec validate 校验主 spec 合并结果。
