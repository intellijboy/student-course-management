# Design

## Context

现有约 116 个测试方法、约 20 个测试类均无 `@DisplayName`，报告仅显示英文方法名。动机与范围见 proposal.md - Why/What Changes。本设计为纯测试注解增强（行为不变，skip_specs）。

## Goals / Non-Goals

**Goals:**

- 全部测试类添加类级 `@DisplayName`（中文，对应模块与层级）。
- 全部测试方法添加方法级 `@DisplayName`（行为描述式中文名）。
- 保持测试逻辑、断言与业务代码完全不变。

**Non-Goals:**

- 不修改测试断言、业务代码或 REST 行为。
- 不重命名测试方法（方法名保持英文，`@DisplayName` 仅作用于显示层）。
- 不改动 `librarylending` / `studentcourse` 任何主代码。

## Decisions

### D1. 命名规范（行为描述式中文名）

- **方法级**：以"场景 + 期望行为"一句话描述，动词开头，如：
  - `shouldRejectOutOfRangeScoreOnRegister` → `@DisplayName("登记成绩时拒绝越界分数")`
  - `borrowRejectsUnknownReader` → `@DisplayName("借阅时拒绝不存在的读者")`
  - `registerBookReturns201` → `@DisplayName("登记图书返回 201")`
- **类级**：对应模块与层级，如 `图书聚合测试`、`借阅应用服务测试`、`图书借阅接口测试`。

### D2. 覆盖范围

- 全部 `src/test/java` 下的测试类与方法（`librarylending`、`studentcourse`、启动类测试）。
- 已使用其他测试注解（`@SpringBootTest`、`@DirtiesContext`、`@EventListener` 等）的类不变，仅追加 `@DisplayName`。

### D3. 验证方式

- 注解不影响执行逻辑，运行 `./gradlew test` 全量通过即验证完成；可抽查测试报告确认中文名生效。

## Risks / Trade-offs

- [个别中文名与实际行为描述不贴切] → 以方法名与断言为准逐条编写，实现时按任务核对。
- [工作量较大（116 个方法）] → 按测试类分批添加，每批编译/测试验证。

## Migration Plan

- 无数据迁移；回滚为还原测试文件即可（仅注解变更，风险低）。

## Open Questions

- 无。命名规范、覆盖范围已明确。
