# Design

## Context

- 见 `proposal.md - Why`。工程已从 RBAC 脚手架演化为以"学生课程成绩管理"为核心的 DDD 分层示例，现名 `rbac-openspec-demo` 与业务内容不符。
- 技术栈：Java 21、Spring Boot 4.1.2、Kotlin DSL 构建、Gradle Wrapper。
- 本次为纯重命名重构：项目标识（rootProject.name、主应用类、页面标题）改名，行为零变化。

## Goals / Non-Goals

**Goals:**
- 统一项目标识为新英文名 `student-course-management`：Gradle 根项目名、主应用类名、页面标题。
- 保持 `cn.intellijboy.demo` Java 包结构不变，最小化改动面。
- 改名后编译、全部测试、页面功能不受影响。

**Non-Goals:**
- 不调整 Java 包路径（`cn.intellijboy.demo`）。
- 不修改任何领域、应用、基础设施、Web 层代码与 REST API。
- 不改动已归档的 OpenSpec 变更记录中的历史引用。
- 不引入新功能、新依赖。

## Decisions

### D1. 新项目名：`student-course-management`
- `settings.gradle.kts` 的 `rootProject.name = "student-course-management"`。
- 依据：与已归档 capability `student-course-management` 语义一致，直接表达工程业务内容；kebab-case 符合 Gradle 项目命名惯例。
- **备选**：`campus-course-manager`、`gradebook` 等——更简短，但与已有 capability 语义不一致，易造成 spec 与工程名称脱节，不采用。

### D2. 主应用类与测试类同步重命名
- `src/main/java/cn/intellijboy/demo/RbacOpenspecDemoApplication.java` → `StudentCourseManagementApplication.java`（类名、文件名同步）。
- `src/test/java/cn/intellijboy/demo/RbacOpenspecDemoApplicationTests.java` → `StudentCourseManagementApplicationTests.java`（类名、文件名同步，`@SpringBootTest` 内容不变）。
- 包名不变：`cn.intellijboy.demo`。
- **备选**：仅改 `rootProject.name` 不动类名——命名仍不一致，违背重构目标，不采用。

### D3. 前端页面标题对齐
- `src/main/resources/static/index.html` 的 `<title>` 由"学生课程成绩管理系统"改为"学生课程管理系统（Student Course Management）"。
- 说明：保留中文主标题以便用户识别，附英文对齐新项目名。
- **备选**：仅改英文不改中文标题——不满足"对齐新名称"诉求，不采用。

### D4. 构建产物名随之变化
- `bootJar` 产物默认以 `rootProject.name` 命名，改名后 jar 包名同步变化（`student-course-management-*.jar`），无需额外配置。

## Risks / Trade-offs

- [IDE/构建缓存引用旧名] → 重命名后清理 `.gradle`/IDE 缓存或直接重建；`./gradlew clean` 规避陈旧产物。
- [历史文档/归档记录引用旧名] → 归档变更属历史记录，保持原样，不影响现状。
- [外部使用者引用 jar 产物名] → 示例项目无外部消费者，可接受。

## Migration Plan

- 无数据迁移。构建链路验证：`./gradlew clean build`（编译 + 全部测试 + 打包）通过即视为完成。
- 回滚策略：恢复 `settings.gradle.kts`、主应用类与测试类文件为原名即可，改动可逆。

## Open Questions

- 无。
