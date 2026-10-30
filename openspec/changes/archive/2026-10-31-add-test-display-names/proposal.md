# Proposal

## Why

本工程是 DDD 教学示例，包含约 116 个单元/集成测试，但所有测试仅以英文驼峰方法名展示（如 `shouldRejectOutOfRangeScoreOnRegister`、`borrowRejectsUnknownReader`），在 IDE 与测试报告中可读性差，难以与非开发者交流，也无法与 `docs/ddd-tutorials/` 教程中的中文"测试地图"直接对照。通过为测试添加中文 `@DisplayName`，让测试结果成为可人读的中文文档，提升工程作为教学示例的可用性。

## What Changes

- 为**全部**测试类（约 20 个）添加类级 `@DisplayName`（中文名，对应模块与层级）。
- 为**全部** `@Test` 测试方法（约 116 个）添加方法级 `@DisplayName`，采用**行为描述式**中文名（以"场景 + 期望行为"一句话描述，如"登记成绩时拒绝越界分数"）。
- 仅添加注解，**不改任何测试逻辑、断言与业务代码**；测试方法名保持英文不变。

## Capabilities

### New Capabilities

<!-- 纯测试可读性改造，无新 capability -->

无

### Modified Capabilities

<!-- 行为不变：仅测试注解增强，不改变任何系统行为，因此设置 skip_specs: true，不生成 spec delta。 -->
无

## Impact

- **测试代码**：`src/test` 下全部测试类与方法添加 `@DisplayName` 注解。
- **行为**：系统行为、测试断言、REST API 均不变。
- **规格**：无 spec 变更（skip_specs）。
- **文档**：可选核对 `docs/ddd-tutorials/` 测试地图（如有必要同步中文名）。
