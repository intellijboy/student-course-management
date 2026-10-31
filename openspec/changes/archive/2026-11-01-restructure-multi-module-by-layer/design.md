# Design

## Context

动机见 proposal.md。现状为单一 Gradle 模块：两个限界上下文（`librarylending` 34 domain / 6 application / 16 infrastructure / 14 web；`studentcourse` 14 domain / 3 application / 13 infrastructure / 12 web）全部平铺于同一 `src/main/java`，打进同一个 JAR；唯一跨上下文依赖是 `ReaderCatalogGatewayAdapter`（librarylending.infrastructure.acl → studentcourse，保防层查读者）。根启动类 `StudentCourseManagementApplication` 位于 `cn.intellijboy.demo`，下游无上下文归属。

**盘点的两个关键事实：**
1. 两个上下文**没有共享的业务类/工具/常量**——`sc-common` 模块当前**没有可迁移的存量实例**，它在本变更中作为新的共享基座被引入（承载未来跨上下文契约），而非整理既有共享代码。
2. libraries 间唯一真实跨模块依赖（防腐层）因其位于两个上下文的**同一层 infrastructure**，按层拆模块后仍落在同一 `sc-infrastructure` 模块内，**无需靠模块间依赖表达**，这也正是"按层拆"能天然化解该耦合的原因。

## Goals / Non-Goals

**Goals:**
- 用 Gradle 模块从物理上固化 DDD 依赖方向：`interfaces → application → domain ← infrastructure`，任一方向违规在构建期编译失败。
- 保持模块化单体：单个可运行 Spring Boot JAR，不拆进程、不拆数据库，静态前端与 `application.yml`/`db/*.sql` 归属单一装配模块。

**Non-Goals:**
- 不引入网络级微服务拆分、服务注册/网关、RPC、事件总线（既定决策：单进程模块化单体）。
- 不拆分数据库（既定决策：不分库）。
- 不改任何包名、不重命名类、不改业务代码内容（纯目录迁移 + 构建结构重排）。
- 不为填充 `sc-common` 而臆造共享抽象
- 
- ；`sc-common` 的骨架成立，但内容仅在确有跨上下文共享时放入。

## Decisions

### D1：模块划分 —— `sc-common` + 四个层模块 + `sc-app` 装配模块（共 6 个 Gradle 子模块）

每个 Gradle 子模块名统一加 `sc-` 前缀（对应根工程 `student-course` 缩写），与本工程命名风格一致。

```
student-course-management/            (根聚合，仅 settings + 根构建)
├── sc-common/          : 跨上下文共享契约/工具/基类（纯 Java，不依赖任何模块、不依赖 Spring Boot）
├── sc-domain/          : 两上下文的领域模型、仓储端口、领域服务、领域事件、ACL 端口、领域异常
├── sc-application/     : 两上下文的应用服务、查询服务、用例编排（依赖 sc-domain）
├── sc-infrastructure/  : JPA 实体与映射、Spring Data 仓储实现、防腐层适配器（依赖 sc-domain，实现其端口）
├── sc-interfaces/      : Spring Web 控制器、请求/响应视图、全局异常处理（依赖 sc-application + sc-domain + sc-infrastructure）
└── sc-app/             : 装配模块：@SpringBootApplication 启动类、application.yml、db/*.sql、static/**（聚合 sc-interfaces）
```

- 选 `sc-domain`/`sc-application`/`sc-infrastructure`/`sc-interfaces` 而非上下文名，是为「物理层隔离 + 单向依赖」的**教学清晰度**（用户既定决策："按层拆 4+1"）。两个上下文仍以原包名 `cn.intellijboy.demo.librarylending` / `cn.intellijboy.demo.studentcourse` 在各层模块内共存。
- 备选（已否决）：按上下文拆两个大模块。虽物理隔离戏仿微服务，但会让 `ReaderCatalogGatewayAdapter` 成为跨模块依赖、且 `application`/`web` 在上下文内部仍需再次禁止跨层，模块矩阵更复杂；与"防混淆""教学"目标相悖。

### D2：依赖方向与 Gradle 约束

- `sc-domain` 不依赖任何模块（`api(project(":sc-common"))` 可选，仅当领域层用到共享契约）。
- `sc-application` → `sc-domain`、`sc-common`。
- `sc-infrastructure` → `sc-domain`、`sc-common`。
- `sc-interfaces` → `sc-application`、`sc-domain`、`sc-infrastructure`（`sc-infrastructure` 用于装配 bean / 供 `sc-app` 聚合）。
- `sc-app` → `sc-interfaces`，负责启动。
- 根级不强加"禁止跨模块"的 `dependencies` 条(human maven-style rules)，而是通过**严格只声明所需依赖** + 依赖反转实现约束：谁在源码里 import 了未声明的模块类，Gradle 直接编译失败。
- 各层模块设为普通 `java-library`（**不** apply Spring Boot 或扫描其自动配置）；仅 `sc-app` 模块 apply `org.springframework.boot` 与 `io.spring.dependency-management`。这与当前 `build.gradle.kts` 中 Boot 插件位置一致（仅在装配层）。

### D3：`sc-common` 模块的边界（如实、克制）

- **当前盘点无存量共享类**，因此 `sc-common` 本期**以最小骨架建立**（包 `cn.intellijboy.demo.common`），不预造接口/工具。
- 若发现跨上下文确实需要的契约（如统一响应封装、跨上下文事件定义、通用值对象），归于此模块；否则保持近空。
- 这是对"抽一层公共契约模块"决策的落地：**模块成立，内容按需填充**，避免空洞抽象（见 Non-Goals）。
- 备选（已否）：把 `ReaderCatalogGateway`/`ReaderRef` 上提到 `sc-common`。否决原因：它们属领域 ACL 术语，是本上下文领域职责，不应为"有内容可放"而挪出（保持 D1/非目标中的"不改领域归属"）。

### D4：装配与资源归属

- 启动仪式资源集中放 `sc-app`（`src/main/resources/application.yml`、`db/schema.sql`、`db/data.sql`、`static/**`）。
- `sc-interfaces` 不再持有 resources/static（移到 sc-app），避免装配资源与接口层代码混居。
- 测试资源同理：`sc-app` 或对应模块的 `src/test/resources` 按需放置；跨上下文集成测试（如 `LendingEndToEndTest`）留在 `sc-interfaces` 或 `sc-app` 的测试源集。

## Risks / Trade-offs

- [迁移漏改包内跨目录引用（如 `component-scan`、配置文件 `classpath:` 路径）] → 全量 `./gradlew build` 回归 + 启动冒烟验证；`@SpringBootApplication` 扫描基包 `cn.intellijboy.demo` 不变，跨模块类仍被同一扫描基包覆盖。
- [`sim`（静态资源/start 脚本）路径在资源迁移后失效] → 启动后 curl 静态页与既有端点回归；`static/` 路径随 app 模块 resources 保留原相对路径。
- [`sc-common` 近空，模块定义看似"没用"] → 在 design/tasks 中明确其定位为共享基座；不因其近空而删除或预造内容。
- [测试类按层散落各模块，`test` 任务体系变化] → 各模块保留自身 `src/test`；聚合构建 `./gradlew build` 统一执行全部测试，数量与断言结果不变。

## Migration Plan

1. **结构调整前置**：确认 `settings.gradle.kts` 插件管理仓库（镜像）可被子模块复用；逐模块建骨架。
2. **迁移顺序**：先建 `sc-common`/`sc-domain` 两个无依赖模块 → 再 `sc-application` → 再 `sc-infrastructure` → 再 `sc-interfaces` → 最后 `sc-app`（装配启动）。每步以该模块 `compileJava`/`compileKotlin` 通过为验收。
3. **依赖标注唯一化**：把 `ReaderCatalogGatewayAdapter` 依赖的 `JpaStudentRepository` 解析为同 `sc-infrastructure` 模块内的类，无需新依赖声明。
4. **收敛验证**：移除原根 `src/` 后 `./gradlew clean build` 全绿；启动应用 curl 冒烟（`/`、`/api/students`、`/api/books`）确认行为不变。
5. **回滚**：`git revert`（恢复单一 `src/` 结构），本变更无数据/部署迁移。

## Open Questions

（无）—— 模块维度、依赖方向、`sc-common` 边界、装配归属均已随既定决策确定；`sc-common` 的具体加入内容留待演化（设计层面刻意不预决）。