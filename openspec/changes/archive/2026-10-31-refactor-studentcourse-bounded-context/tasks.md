# Tasks

## 1. 主代码包结构迁移（业务域优先）

- [x] 1.1 迁移 `cn.intellijboy.demo.web.studentcourse` → `cn.intellijboy.demo.studentcourse.web`（`git mv` + 更新 package/import 声明）——验证：`./gradlew compileJava` 通过
- [x] 1.2 迁移 `cn.intellijboy.demo.application.studentcourse` → `cn.intellijboy.demo.studentcourse.application`（应用服务、事件引用）——验证：`./gradlew compileJava` 通过
- [x] 1.3 迁移 `cn.intellijboy.demo.domain.studentcourse` → `cn.intellijboy.demo.studentcourse.domain`（model/exception/repository/event）——验证：`./gradlew compileJava` 通过
- [x] 1.4 迁移 `cn.intellijboy.demo.infrastructure.studentcourse` → `cn.intellijboy.demo.studentcourse.infrastructure`（repository/persistence/mapper/event）——验证：`./gradlew compileJava` 通过
- [x] 1.5 更新 `librarylending` 防腐层 `ReaderCatalogGatewayAdapter` 对 `JpaStudentRepository` 的 import（`infrastructure.studentcourse` → `studentcourse.infrastructure`）——验证：`./gradlew compileJava` 通过

## 2. 测试包路径迁移

- [x] 2.1 迁移 `src/test` 下 `cn.intellijboy.demo.{web,application,domain,infrastructure}.studentcourse` 测试至 `cn.intellijboy.demo.studentcourse.{web,application,domain,infrastructure}`（package 与 import 同步更新）——验证：`./gradlew test` 通过

## 3. 回归与文档核对

- [x] 3.1 运行全量测试回归，确认学生/课程/成绩 REST 行为不变——验证：`./gradlew test` 全绿，接口语义无变化
- [x] 3.2 核对 `git status` 迁移识别为 rename，并检查 `docs/ddd-tutorials/` 中 `studentcourse` 代码路径描述与新结构一致（如需更新则修正）——验证：`git status` 显示 rename，教程文档路径正确

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更（skip_specs 重构，无 spec 合并）。
- 归档后运行 openspec validate 校验主 spec（应仍通过）。
