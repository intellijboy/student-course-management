# Tasks

## 1. 模块骨架与设置

- [x] 1.1 在 `settings.gradle.kts` 声明全部子模块（include `sc-common`/`sc-domain`/`sc-application`/`sc-infrastructure`/`sc-interfaces`/`sc-app`），保留根聚合名 `student-course-management`；验证：`./gradlew projects` 列出 6 个子模块无报错。
- [x] 1.2 迁移根 `build.gradle.kts` 的分层依赖声明到各子模块：为 `sc-common`(纯 java-library)、`sc-domain`(java-library)、`sc-application`(java-library)、`sc-infrastructure`(java-library)、`sc-interfaces`(java-library)、`sc-app`(apply Spring Boot) 建立独立 `build.gradle.kts`；验证：`./gradlew help` 各模块可解析、无插件加载错误。
- [x] 1.3 明确各模块依赖声明（sc-domain 无依赖；sc-application→sc-domain+sc-common；sc-infrastructure→sc-domain+sc-common；sc-interfaces→sc-application+sc-domain+sc-infrastructure+sc-common；sc-app→sc-interfaces），`sc-common` 建 `cn.intellijboy.demo.common` 空包骨架；验证：`./gradlew :sc-common:compileJava` `:sc-domain:compileJava` 通过。

## 2. 领域层迁移

- [x] 2.1 将两个上下文全部 `domain` 类（librarylending 34 + studentcourse 14，含 model/event/exception/repository/service/acl）从 `src/main/java/cn/.../demo/{context}/domain/` 迁至 `sc-domain/src/main/java/cn/.../demo/{context}/domain/`，包名不变；验证：对应源码仅移动目录、包名未变，`:sc-domain:compileJava` 通过。
- [x] 2.2 迁移 domain 层后续测试（如 `LoanTest`/`ReservationTest`/`BorrowingPolicyAndFactoryTest` 等纯领域单测）到 `sc-domain/src/test/`；验证：`:sc-domain:test` 通过。

## 3. 应用层迁移

- [x] 3.1 将两上下文全部 `application` 类（librarylending 6 + studentcourse 3，应用服务/查询服务/配置）迁至 `sc-application/src/main/java/.../{context}/application/`；改其 `import` 指向 `sc-domain` 模块可用的类；验证：`:sc-application:compileJava` 通过、仅依赖 sc-domain/sc-common。
- [x] 3.2 迁移应用层测试（`LoanApplicationServiceTest`/`CatalogApplicationServiceTest`/`LendingQueryServiceTest`/`StudentCourseApplicationServiceTest` 等）到 `sc-application/src/test/`；验证：`:sc-application:test` 通过。（决策：studentcourse 的 3 个 `@SpringBootTest` 集成测试因依赖完整 Spring 上下文，改迁至 `sc-app` 测试源集——见 D4。）

## 4. 基础设施层迁移

- [x] 4.1 将两上下文全部 `infrastructure` 类（librarylending 16 + studentcourse 13，JPA 实体/映射/Spring Data 仓储/防腐层适配器/实现）迁至 `sc-infrastructure/src/main/java/.../{context}/infrastructure/`；使 `ReaderCatalogGatewayAdapter` 依赖的 `JpaStudentRepository` 解析为同模块内类；验证：`:sc-infrastructure:compileJava` 通过。
- [x] 4.2 迁移基础设施集成测试（`LendingRepositoryIntegrationTest`/`RepositoryIntegrationTest`）及其所需测试资源（`src/test/resources/application.yml`）到 `sc-infrastructure/src/test/`；验证：`:sc-infrastructure:test` 通过。（决策：这两个 `@SpringBootTest` 集成测试因依赖完整上下文，与 D4 一致改迁至 `sc-app` 测试源集，测试配置 `sc-app/src/test/resources/application.yml`。）

## 5. 接口层与装配层迁移

- [x] 5.1 将两上下文全部 `web` 类（librarylending 14 + studentcourse 12，控制器/视图/请求响应/异常处理）迁至 `sc-interfaces/src/main/java/.../{context}/web/`；验证：`:sc-interfaces:compileJava` 通过、依赖 sc-application+sc-domain+sc-infrastructure。
- [x] 5.2 迁移接口层控制器/端到端测试（`*ControllerTest`/`LendingEndToEndTest`）到 `sc-interfaces/src/test/`；验证：`:sc-interfaces:test` 通过。（决策：此 4 个测试均为 `@SpringBootTest`，与 D4 一致改迁至 `sc-app` 测试源集；`sc-interfaces` 仅含主类无测试源。）
- [x] 5.3 将启动类 `StudentCourseManagementApplication`、`src/main/resources/application.yml`、`db/schema.sql`、`db/data.sql`、`static/**`（前端资源）迁移到 `sc-app` 模块（`sc-app/src/main/`）；验证：`sc-app` 启动配置可被解析。

## 6. 集成回归与清理

- [x] 6.1 删除根级残留的 `src/main` 与 `src/test`（已全部迁空）；验证：根目录不再有 `src/`，`./gradlew clean build` 全模块编译测试通过（BUILD SUCCESSFUL）。
- [x] 6.2 启动 `sc-app` 冒烟验证：`./gradlew :sc-app:bootRun` 或运行打包 JAR，curl `/`（前端页）、`/api/students`、`/api/books`、`/api/loans/overdue` 返回与迁移前一致；验证：各端点与静态页正常、无 404/500。
- [x] 6.3 复核变更面：`git diff --stat` 应为纯文件路径移动 + 新增构建文件，业务 `.java` 内容除包内 import 外无逻辑改动；验证：diff 复核无行为变更，全部测试通过。

## Workflow follow-up

- 全量 `./gradlew build` 绿灯后，将本变更 `restructure-multi-module-by-layer` 归档（`/opsx:archive`），确认无激活变更。