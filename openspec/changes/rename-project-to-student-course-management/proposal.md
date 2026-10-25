# Proposal

## Why

当前项目名 `rbac-openspec-demo` 源于最初以 RBAC + OpenSpec 示例为定位的脚手架，但工程已演进为以"学生课程成绩管理"为核心的 DDD 分层示例，现名与工程实际内容不符。需要更换一个与业务内容一致的英文项目名，消除命名误导。

## What Changes

- 将 Gradle 根项目名由 `rbac-openspec-demo` 改为 `student-course-management`（`settings.gradle.kts` 的 `rootProject.name`）。
- 将主应用类 `RbacOpenspecDemoApplication` 重命名为 `StudentCourseManagementApplication`，测试类 `RbacOpenspecDemoApplicationTests` 同步重命名为 `StudentCourseManagementApplicationTests`（类名与文件名一致）。
- 将前端页面标题由"学生课程成绩管理系统"对齐为新项目名，如"学生课程管理系统（Student Course Management）"（`src/main/resources/static/index.html` 的 `<title>`）。
- **BREAKING**：`bootJar` 产物名随 `rootProject.name` 变化（由 `rbac-openspec-demo-*.jar` 变为 `student-course-management-*.jar`）。

## Capabilities

### New Capabilities
<!-- 无新增 capability -->

### Modified Capabilities
<!-- 无 capability 级行为变更：本变更仅涉及项目标识（名称/类名/页面标题）的纯重构，
    学生/课程/成绩 CRUD、成绩领域规则、REST API 与页面功能行为均保持不变。
    因此设置 skip_specs: true，不生成 spec delta。 -->

## Impact

- **代码**：`settings.gradle.kts`（`rootProject.name`）；`src/main/java/cn/intellijboy/demo/RbacOpenspecDemoApplication.java` 重命名为 `StudentCourseManagementApplication.java`；`src/test/java/cn/intellijboy/demo/RbacOpenspecDemoApplicationTests.java` 重命名为 `StudentCourseManagementApplicationTests.java`。
- **前端**：`src/main/resources/static/index.html` 的 `<title>` 文案。
- **构建产物**：`bootJar` 包名与 `rootProject.name` 同步变化。
- **行为**：REST API、领域规则、页面功能均无变化；归档的 OpenSpec 变更记录中历史引用旧名，不改动。
