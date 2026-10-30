# Proposal

## Why

`/demo` 空壳端点是早期脚手架的连通性验证产物。Book 示例移除后，工程仅剩学生课程成绩业务模块，`DemoController` 成为 `web` 包中最后一个与业务无关的示例类，继续保留与「仅保留学生课程成绩模块」的工程定位不一致。

## What Changes

- **BREAKING**：删除空壳端点 `GET /demo`，响应 `demo ok` 的行为一并移除。
- 删除 `web/DemoController.java` 源码。
- 删除 `StudentCourseManagementApplicationTests` 中的 `demoEndpointReturnsSuccess` 测试方法及随之无用的静态导入，保留 `contextLoads`。
- 从 `build-infrastructure` capability 中移除「空壳应用启动与 HTTP 响应」需求（`/demo` 是其唯一实现），并以「应用可启动」需求承接其中仍成立的启动验证能力。
- 同步调整主 spec 的 Purpose 表述（不再提及「空壳应用」）。
- 学生课程成绩模块（`student-course-management`）不受影响。

## Capabilities

### New Capabilities
<!-- 本变更不引入新 capability。 -->

### Modified Capabilities
- `build-infrastructure`: 移除「空壳应用启动与 HTTP 响应」需求，新增「应用可启动」需求（应用可直接启动并绑定端口，不再要求空壳端点返回成功响应）；其余构建基础设施需求不变。

## Impact

- **代码**：删除 `cn.intellijboy.demo.web.DemoController`；修改 `cn.intellijboy.demo.StudentCourseManagementApplicationTests`（删 1 个测试方法与无用 import）。无其他代码引用。
- **API**：`GET /demo` 下线；学生课程成绩相关端点行为不变。
- **规格**：`openspec/specs/build-infrastructure/spec.md` 需求「空壳应用启动与 HTTP 响应」替换为「应用可启动」（需求数仍为 7），Purpose 表述同步更新。
- **依赖/系统**：无新增依赖，无数据迁移。
- **不在范围**：`application.yml` 中的 `jdbc:h2:mem:demo` 为数据库名，与 `/demo` 无关，保持不变；`openspec/changes/archive/` 历史归档不动。
