# Proposal

## Why

现有 `studentcourse` 模块按**技术层次优先**组织包：`cn.intellijboy.demo.{web,application,domain,infrastructure}.studentcourse`——分层位于包路径第一级，限界上下文被降级为子目录。而 `librarylending` 模块采用**业务域优先**：`cn.intellijboy.demo.librarylending.{web,application,domain,infrastructure}`。两个并列上下文组织方式不一致，削弱了"限界上下文是第一级组织单元"的 DDD 表达，也让读者对照学习时产生混淆。需将 `studentcourse` 对齐为业务域优先，保持工程一致并强化 DDD 教学示范。

## What Changes

- **包结构重组（业务域优先）**：将 `studentcourse` 从 `cn.intellijboy.demo.{web,application,domain,infrastructure}.studentcourse` 迁移为 **`cn.intellijboy.demo.studentcourse.{web,application,domain,infrastructure}`**，与 `librarylending` 完全对齐。
- **同步更新跨模块引用**：`librarylending` 防腐层适配器 `ReaderCatalogGatewayAdapter` 引用的 `cn.intellijboy.demo.infrastructure.studentcourse.repository.JpaStudentRepository` 需更新为新包路径。
- **测试包路径迁移**：`src/test` 下 `studentcourse` 相关测试同步迁移至新包结构。
- **行为不变（纯重构）**：REST API、页面、领域规则与查询语义均不改变，仅代码组织调整。
- **文档核对**：检查并更新 `docs/ddd-tutorials/` 中对 `studentcourse` 代码路径的描述（如适用）。

## Capabilities

### New Capabilities

<!-- 纯重构，无新 capability -->

无

### Modified Capabilities

<!-- 行为不变：仅包结构重组，不改变任何需求语义，因此设置 skip_specs: true，不生成 spec delta。 -->
无

## Impact

- **代码**：`src/main/java/cn/intellijboy/demo/studentcourse/**` 全量包路径迁移（git 识别为大量 rename）；`librarylending` 防腐层适配器 import 更新；`src/test/**` 测试包路径迁移。
- **构建**：无新依赖；编译与测试全量通过（`./gradlew test`）。
- **行为**：学生/课程/成绩 REST API、前端页面、领域规则均不变。
- **规格**：`student-course-management` spec 不变（行为不变，skip_specs）。
