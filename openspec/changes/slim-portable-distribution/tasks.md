# Tasks

## 1. 构建配置调整

- [x] 1.1 在 `sc-main/build.gradle.kts` 的 `javapackager` 块将 `customizedJre(false)` 改为 `customizedJre(true)`，验证执行 `./gradlew :sc-main:help --dry-run` 配置阶段无报错
- [x] 1.2 新增 `additionalModules(listOf("java.logging","java.naming","java.sql","java.xml","java.management","java.prefs","java.instrument","java.security.jgss","java.security.sasl","jdk.crypto.ec","jdk.unsupported","jdk.naming.rmi","java.net.http"))`，验证执行 `./gradlew :sc-main:help --dry-run` 配置阶段无报错
- [x] 1.3 新增 `additionalJlinkArgs(listOf("--compress=zip-6"))`，验证执行 `./gradlew :sc-main:help --dry-run` 配置阶段无报错

## 2. 打包与体积对比

- [x] 2.1 改造前先执行 `./gradlew :sc-main:package` 记录基线：验证 Windows zip 产物约 130MB（或记录实际体积作为对比基准）
- [x] 2.2 改造后重新执行 `./gradlew :sc-main:package`，验证产物仍输出到 `sc-main/build/` 且命名不变（`sc-main-<version>-<platform>.zip|tar.gz`）
- [x] 2.3 对比体积：验证 Windows zip 降至 105MB 以下（且相对基线下降 ≥ 20%），Linux/macOS tar.gz 同比缩小

## 3. 运行冒烟与全量测试

- [x] 3.1 解压产物并以分发包内启动命令启动应用，验证应用正常启动、无 `NoClassDefFoundError` / ServiceLoader 加载失败
- [x] 3.2 通过 Web 接口执行一次业务操作（含 H2 数据库读写），验证 REST API 与数据链路正常
- [x] 3.3 运行 `./gradlew test`，验证全量测试（110+ 项）通过

## 4. 收尾

- [ ] 4.1 提交本次变更（Conventional Commits，含 `Co-Authored-By` 尾注），验证 `git status` 工作区干净
- [ ] 4.2 推送并观察 `.github/workflows/packaging.yml` 三平台打包矩阵，验证各 runner 构建通过且产物体积缩小

## Workflow follow-up

- 变更经评审满足要求后归档 `slim-portable-distribution`。
- 归档后验证 main spec（`build-infrastructure`）已吸收裁剪运行时与体积约束需求。
